package com.hala.myrestaurantapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



// adapter class for custom category list
class CategoryListAdapter extends BaseAdapter {

		private Activity activity;
		private com.hala.myrestaurantapp.ImageLoader imageLoader;
		
		public CategoryListAdapter(Activity act) {
			this.activity = act;
			imageLoader = new com.hala.myrestaurantapp.ImageLoader(act);
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return com.hala.myrestaurantapp.CategoryList.Category_ID.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.category_list_item, null);
				holder = new ViewHolder();
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			

			holder.txtText = (TextView) convertView.findViewById(R.id.txtText);
			holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
			
			holder.txtText.setText(com.hala.myrestaurantapp.CategoryList.Category_name.get(position));
			imageLoader.DisplayImage(com.hala.myrestaurantapp.Utils.AdminPageURL+ com.hala.myrestaurantapp.CategoryList.Category_image.get(position),
					activity, holder.imgThumb);
			
			return convertView;
		}
		
		static class ViewHolder {
			TextView txtText;
			ImageView imgThumb;
		}
		
		
		
	}