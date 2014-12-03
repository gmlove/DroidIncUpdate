package com.comeplus.droidincupdate;

import java.util.List;

public interface UpdateProgressListener {
	public void onExtractProgress(long totalLength, long extractedLength, List<String> filesToExtract,
		List<String> extractedFiles, String currentFile, long currentFileLength, long currentFileExtractedLength);
    public void onExtractEnd();
	public void onDownloadProgress(long totalLength, long downloadedLength);
	public void onDownloadEnd();
	public void onPatchProgress(List<String> filesToPatch, List<String> patchedFiles, String currentFile);
	public void onPatchEnd();
}
