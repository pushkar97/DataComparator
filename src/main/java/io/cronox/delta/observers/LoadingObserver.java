package io.cronox.delta.observers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.cronox.delta.helpers.shellHelpers.ProgressCounter;

@Component
public class LoadingObserver implements PropertyChangeListener {

	@Autowired
	ProgressCounter progressCounter;
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if((int)evt.getOldValue() == 0) 
			progressCounter.reset();
		else 
			progressCounter.display((int)evt.getNewValue(), "Getting data from Data Source..");
	}
}
