package io.cronox.delta.helpers.shellHelpers;
public class ProgressBar {
    private static final String CUU = "\u001B[A";
    private static final String DL = "\u001B[1M";
    
    private String doneMarker = "█";
    private String remainsMarker = "·";
    private String leftDelimiter = "<";
    private String rightDelimiter = ">";
    
    private boolean started = false;
    
    ShellHelper shellHelper;

    public ProgressBar(ShellHelper shellHelper) {
        this.shellHelper = shellHelper;
    }

    public void display(int percentage) {
    	this.display(percentage, "");
    }
    public void display(int percentage, String message) {
        if (!started) {
            started = true;
            shellHelper.getTerminal().writer().println();
        }
        int x = (percentage/2);
        int y = 50-x;

        String done = shellHelper.getSuccessMessage(new String(new char[x]).replace("\0", doneMarker));
        String remains = new String(new char[y]).replace("\0", remainsMarker);

        String progressBar = String.format("%s%s%s%s %d", leftDelimiter, done, remains, rightDelimiter, percentage);

        shellHelper.getTerminal().writer().println(CUU + "\r" + DL + progressBar + "% "+ message);
        shellHelper.getTerminal().flush();
    }

    public void reset() {
        started = false;
    }

	public void setDoneMarker(String doneMarker) {
		this.doneMarker = doneMarker;
	}

	public void setRemainsMarker(String remainsMarker) {
		this.remainsMarker = remainsMarker;
	}

	public void setLeftDelimiter(String leftDelimiter) {
		this.leftDelimiter = leftDelimiter;
	}

	public void setRightDelimiter(String rightDelimiter) {
		this.rightDelimiter = rightDelimiter;
	}

}