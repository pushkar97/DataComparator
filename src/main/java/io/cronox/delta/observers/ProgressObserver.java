package io.cronox.delta.observers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.cronox.delta.helpers.shellHelpers.ProgressBar;

@Component
//@Scope("prototype")
public class ProgressObserver implements PropertyChangeListener {

	@Autowired
	ProgressBar progressBar;
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if((int)evt.getOldValue() > (int)evt.getNewValue()) {
			progressBar.reset();
		}
		String message = "Please wait, Comparison in progress...";

		if ((int)evt.getNewValue() >= 50){
			message = "halfway done!";
		}
		if ((int)evt.getNewValue() >= 90){
			message = "Almost there...";
		}
		if ((int)evt.getNewValue() == 100){
			message = "Completed!";
		}
		progressBar.display((int) evt.getNewValue()," :: " + message);
		
		if((int)evt.getNewValue() >= 100) {
			progressBar.reset();
		}
	}
}
