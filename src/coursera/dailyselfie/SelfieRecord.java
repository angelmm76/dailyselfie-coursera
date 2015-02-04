package coursera.dailyselfie;

import android.graphics.Bitmap;

public class SelfieRecord {
	
	public static final String ITEM_SEP = System.getProperty("line.separator");
	public static final Integer scale = 10;
	
	private String mSelfieDate;
	private Bitmap mThumbnailBitmap;
	private String mFilePath;
	
	public SelfieRecord(Bitmap bitmap, String date, String path) {
		this.mThumbnailBitmap = Bitmap.createScaledBitmap(bitmap,
				bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
		this.mSelfieDate = date;
		this.mFilePath = path;
	}

	public SelfieRecord() {	
	}
	
	public String getDate() {
		return mSelfieDate;
	}

	public void setDate(String date) {
		this.mSelfieDate = date;
	}

	public Bitmap getThumbnail() {
		return mThumbnailBitmap;
	}

	public void setThumbnail(Bitmap mFlagBitmap) {
		this.mThumbnailBitmap= mFlagBitmap;
	}
	
	public String getFilePath() {
		return mFilePath;
	}
	
	public void setFilePath(String Path) {
		this.mFilePath= Path;
	}
	
	public String toString() {
		return mSelfieDate + ITEM_SEP + mFilePath;
	}
}
