package io.cronox.delta.resultGenerator;

import io.cronox.delta.comparators.DataSetComparator;
import io.cronox.delta.helpers.CsvHelpers;
import io.cronox.delta.models.DatasetExtract;
import io.cronox.delta.models.TestCase;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

@Component
public class DefaultComparatorResultGenerator implements ResultGenerator {

    @Autowired
    CsvHelpers csvHelpers;

    @Override
    public String generate(DataSetComparator comp, TestCase test, String path) throws InterruptedException, IOException {
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
        Thread queries = null;
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
        queries = new Thread(() -> {
            try (var srcWriter = new BufferedWriter(new FileWriter(new File(f, "source.query")))){
                srcWriter.write(test.getSourceQuery());
            } catch (IOException e) { e.printStackTrace(); }
            try (var tgtWriter = new BufferedWriter(new FileWriter(new File(f, "target.query")))){
                tgtWriter.write(test.getTargetQuery());
            } catch (IOException e) { e.printStackTrace(); }
        });
        queries.start();


        try(var reportWriter = new BufferedWriter(new FileWriter(new File(f, "report.sh")))){
            reportWriter.write("MATCHED=" + comp.getMatched().size());
            reportWriter.write("\nSOURCE_MISMATCH=" + comp.getSet1().size());
            reportWriter.write("\nTARGET_MISMATCH=" + comp.getSet2().size());
            reportWriter.write("\nSOURCE_DUPLICATES=" + comp.getSet1().getDuplicates().size());
            reportWriter.write("\nTARGET_DUPLICATES=" + comp.getSet2().getDuplicates().size());
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
