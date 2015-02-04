package coursera.dailyselfie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SelfieViewActivity extends ListActivity {
	private AlarmManager mAlarmManager;
	private Intent mNotificationIntent;
	private PendingIntent mNotificationPendingIntent;
	private SelfieViewAdapter mAdapter;

	private static final String TAG = "Proyect-DailySelfie";
	private static final int REQUEST_TAKE_PHOTO = 2;
	private static final String FILE_NAME = "Daily_Selfie_data.txt";
		
	private static final long ALARM_INTERVAL = 2 * 60 * 1000L; // two minutes
	
	private String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the app's user interface. This class is a ListActivity, 
		// so it has its own ListView. ListView's adapter should be a SelfieViewAdapter

		mAdapter = new SelfieViewAdapter(getApplicationContext());
		setListAdapter(mAdapter);
		
		// Set up the alarm
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		// Create alarm
		newAlarm();

		// Enable filtering when the user types in the virtual keyboard
		getListView().setTextFilterEnabled(true);

		// Set an setOnItemClickListener on the ListView
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.i(TAG, "Item clicked");
				Intent showIntent = new Intent(SelfieViewActivity.this, ShowSelfieActivity.class);
				SelfieRecord selectedSelfie = (SelfieRecord) mAdapter.getItem(position);
				String path = selectedSelfie.getFilePath();
				showIntent.putExtra("path", path);
				startActivity(showIntent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Load saved selfies, if necessary
		if (mAdapter.getCount() == 0){
			Log.i(TAG, "Loading items");
			loadItems();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Save selfies
		Log.i(TAG, "Saving items");
		saveItems();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {		
           	
			// Add selfie to list
			Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        
	        SelfieRecord selfie = new SelfieRecord(bitmap, timeStamp, mCurrentPhotoPath);
	        mAdapter.add(selfie);

		}
	}
	
	private void dispatchTakePictureIntent() {
	    
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

	    	// Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	        	Log.i(TAG, "Error, file not created" + ex.getMessage());
	
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	        	Log.i(TAG, "File created");
	        	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "Selfie_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    if (!storageDir.exists()){
	    	storageDir.mkdirs();
	    }
	    Log.i(TAG, "Storage dir: " + storageDir);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    mCurrentPhotoPath = image.getAbsolutePath();
	    Log.i(TAG, "mCurrentphotopath: " + mCurrentPhotoPath);
	    return image;
	}

	// Load stored ToDoItems
	private void loadItems() {
		BufferedReader reader = null;
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			Bitmap bitmap = null;
			String date = null;
			String path = null;

			while (null != (date = reader.readLine())) {
				path = reader.readLine();
				bitmap = BitmapFactory.decodeFile(path);
				mAdapter.add(new SelfieRecord(bitmap, date, path));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Save ToDoItems to file
	private void saveItems() {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

			for (int idx = 0; idx < mAdapter.getCount(); idx++) {
				writer.println(mAdapter.getItem(idx));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}
	
	private void newAlarm() {
		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationIntent = new Intent(SelfieViewActivity.this, AlarmNotificationReceiver.class);

		// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationPendingIntent = PendingIntent.getBroadcast(
				SelfieViewActivity.this, 0, mNotificationIntent, 0);
		
		Log.i(TAG, "Alarm set");
		mAlarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + ALARM_INTERVAL, ALARM_INTERVAL,
				mNotificationPendingIntent);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
		dispatchTakePictureIntent();
		return true;
	}

}
