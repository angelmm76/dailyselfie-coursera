package coursera.dailyselfie;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelfieViewAdapter extends BaseAdapter {

	private ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public SelfieViewAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;
		final int pos = position;

		SelfieRecord curr = list.get(position);

		if (null == convertView) {
	
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.selfie_view, null);
			holder.thumbnail = (ImageView) newView.findViewById(R.id.thumbnail);
			holder.date = (TextView) newView.findViewById(R.id.selfie_date);
			newView.setTag(holder);
			
			Button deleteButton = (Button) newView.findViewById(R.id.button1);
			
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "Selfie deleted", Toast.LENGTH_LONG).show();
					File file = new File(list.get(pos).getFilePath());
					file.delete();
					list.remove(pos);
			        notifyDataSetChanged();
					
				}
			});
			
		} else {
			holder = (ViewHolder) newView.getTag();
		}

		holder.thumbnail.setImageBitmap(curr.getThumbnail());
		holder.date.setText(curr.getDate());

		return newView;
	}
	
	static class ViewHolder {
		ImageView thumbnail;
		TextView date;
	}
	
	public void add(SelfieRecord listItem) {
		list.add(listItem);
		notifyDataSetChanged();
	}
	
	public ArrayList<SelfieRecord> getList(){
		return list;
	}
	
	public void removeAllViews(){
		list.clear();
		this.notifyDataSetChanged();
		
	}
}
