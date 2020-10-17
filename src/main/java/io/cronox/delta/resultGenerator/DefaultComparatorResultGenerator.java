package io.cronox.delta.resultGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.util.function.Function;

import io.cronox.delta.models.DatasetExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.helpers.CsvHelpers;

@Component
public class DefaultComparatorResultGenerator implements ResultGenerator {

    @Autowired
    CsvHelpers csvHelpers;

    @Override
    public String generate(DataSetComparator comp, String path) throws InterruptedException {
        File f = Paths.get(path).toAbsolutePath().toFile();
        if (!f.exists()) {
            f.mkdirs();
        }

        Runnable matched_runnable = () -> {
            csvHelpers.datasetToCsv(comp.getMatched(), new File(f, "matched.csv"), DatasetExtract.DATA);
        };
        Thread matched_thread = new Thread(matched_runnable);
        matched_thread.start();

        Runnable source_mismatch_runnable = () -> {
            csvHelpers.datasetToCsv(comp.getSet1(), new File(f, "Source_mismatch.csv"), DatasetExtract.DATA);
        };
        Thread source_mismatch_thread = new Thread(source_mismatch_runnable);
        source_mismatch_thread.start();

        Runnable target_mismatch_runnable = () -> {
            csvHelpers.datasetToCsv(comp.getSet2(), new File(f, "Target_mismatch.csv"), DatasetExtract.DATA);
        };
        Thread target_mismatch_thread = new Thread(target_mismatch_runnable);
        target_mismatch_thread.start();

        Runnable source_duplicates_runnable = () -> {
            csvHelpers.datasetToCsv(comp.getSet1(), new File(f, "Source_duplicates.csv"), DatasetExtract.DUPLICATES);
        };
        Thread source_duplicates_thread = new Thread(source_duplicates_runnable);
        source_duplicates_thread.start();

        Runnable target_duplicates_runnable = () -> {
            csvHelpers.datasetToCsv(comp.getSet2(), new File(f, "Target_duplicates.csv"), DatasetExtract.DUPLICATES);
        };
        Thread target_duplicates_thread = new Thread(target_duplicates_runnable);
        target_duplicates_thread.start();

        matched_thread.join();
        source_mismatch_thread.join();
        target_mismatch_thread.join();
        source_duplicates_thread.join();
        target_duplicates_thread.join();

        return f.getAbsolutePath();
    }

}
