package io.cronox.delta.resultGenerator;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.helpers.CsvHelpers;
import io.cronox.delta.models.DatasetExtract;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;

@Component
public class DefaultComparatorResultGenerator implements ResultGenerator {

    @Autowired
    CsvHelpers csvHelpers;

    @Override
    public String generate(DataSetComparator comp, String path) throws InterruptedException {
        File f = Paths.get(path).toAbsolutePath().toFile();
        if (!f.exists()) {
            var mkd = f.mkdirs();
            if(!mkd)
                return "failed to create directory to store results";
        }

        Thread matched_thread = null;
        Thread source_mismatch_thread = null;
        Thread target_mismatch_thread = null;
        Thread source_duplicates_thread = null;
        Thread target_duplicates_thread = null;

        if(comp.getMatched().size() != 0) {
            Runnable matched_runnable = () -> csvHelpers.datasetToCsv(comp.getMatched(), new File(f, "matched.csv"), DatasetExtract.DATA);
            matched_thread = new Thread(matched_runnable);
            matched_thread.start();
        }

        if(comp.getSet1().size() != 0){
            Runnable source_mismatch_runnable = () -> csvHelpers.datasetToCsv(comp.getSet1(), new File(f, "Source_mismatch.csv"), DatasetExtract.DATA);
            source_mismatch_thread = new Thread(source_mismatch_runnable);
            source_mismatch_thread.start();
        }

        if(comp.getSet2().size() != 0) {
            Runnable target_mismatch_runnable = () -> csvHelpers.datasetToCsv(comp.getSet2(), new File(f, "Target_mismatch.csv"), DatasetExtract.DATA);
            target_mismatch_thread = new Thread(target_mismatch_runnable);
            target_mismatch_thread.start();
        }

        if(comp.getSet1().getDuplicates().size() != 0) {
            Runnable source_duplicates_runnable = () -> csvHelpers.datasetToCsv(comp.getSet1(), new File(f, "Source_duplicates.csv"), DatasetExtract.DUPLICATES);
            source_duplicates_thread = new Thread(source_duplicates_runnable);
            source_duplicates_thread.start();
        }

        if(comp.getSet2().getDuplicates().size() != 0) {
            Runnable target_duplicates_runnable = () -> csvHelpers.datasetToCsv(comp.getSet2(), new File(f, "Target_duplicates.csv"), DatasetExtract.DUPLICATES);
            target_duplicates_thread = new Thread(target_duplicates_runnable);
            target_duplicates_thread.start();
        }
        if(matched_thread != null)
            matched_thread.join();
        if(source_mismatch_thread != null)
            source_mismatch_thread.join();
        if(target_mismatch_thread != null)
            target_mismatch_thread.join();
        if(source_duplicates_thread != null)
            source_duplicates_thread.join();
        if(target_duplicates_thread != null)
            target_duplicates_thread.join();

        return f.getAbsolutePath();
    }

}
