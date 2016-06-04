package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.y2w.uikit.customcontrols.imageview.CircleImageView;
import com.y2w.uikit.customcontrols.imageview.HeadImageView;
import com.y2w.uikit.utils.ImagePool;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppContext;

public class GroupMemberAdapter extends BaseAdapter implements SectionIndexer{
	public List<SortModel> list = null;
	private Context context;
	public String type;//display;select
	
	public GroupMemberAdapter(Context context, List<SortModel> list, String type) {
		this.list = list;
		this.type = type;
		this.context = context;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return list == null ? 0 : this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.contactor_chooser_item, null);
			viewHolder.hiv_header = (HeadImageView) view.findViewById(R.id.hiv_image);
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.tvCatalog = (TextView) view.findViewById(R.id.tv_catalog);
			viewHolder.ivSelectedIcon = (ImageView) view.findViewById(R.id.iv_selector);
			viewHolder.vDivider = (View) view.findViewById(R.id.v_divider);
			viewHolder.tv_circle_name = (TextView) view.findViewById(R.id.tv_image_name);
			viewHolder.rlContactorListItem = (RelativeLayout) view.findViewById(R.id.rl_list_item);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		try{
			//设置目录
			this.setCalalog(viewHolder, view, position, mContent);
			//设置选择状态
			this.setSelectorState(viewHolder, view, mContent);
			//设置名称
			this.setBase(viewHolder, view, mContent);

		}catch(Exception e){
			
		}
		return view;

	}

	private void setCalalog(ViewHolder viewHolder, View view, int position, SortModel mContent){
		//根据position获取分类的首字母的Char ascii值ֵ
		int section = getSectionForPosition(position);
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvCatalog.setText(mContent.getSortLetters());
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
		}
	}

	private void setBase(ViewHolder viewHolder, View view, SortModel model){
		viewHolder.tvName.setText(model.getName());
		viewHolder.hiv_header.loadBuddyAvatarbyurl(model.getAvatarUrl(), R.drawable.default_person_icon);
	}
	
	private void setSelectorState(ViewHolder viewHolder, View view, SortModel mContent){
		if("display".equals(type)){
			viewHolder.ivSelectedIcon.setVisibility(View.GONE);
		}else{
			if("true".equals(mContent.getSelectedStatus())){
				viewHolder.ivSelectedIcon.setBackgroundResource(R.drawable.checked);
			}else{
				viewHolder.ivSelectedIcon.setBackgroundResource(R.drawable.unchecked);
			}
			viewHolder.ivSelectedIcon.setVisibility(View.VISIBLE);
		}
	}

	final static class ViewHolder {
		TextView tvCatalog;
		HeadImageView hiv_header;
		TextView tvName;
		ImageView ivSelectedIcon;
		View vDivider;
		RelativeLayout rlContactorListItem;
		TextView tv_circle_name;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值ֵ
	 */
	public int getSectionForPosition(int position) {
		try{
			if(list.get(position).getSortLetters() != null &&
				!list.get(position).getSortLetters().equals(""))
				return list.get(position).getSortLetters().charAt(0);
			else
				return -1;
		}catch(Exception e){
			return -1;
		}
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		try{
		if(section == -1)
			return -1;
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			if(sortStr != null && !sortStr.equals("")){
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
		}
		}catch(Exception e){
			
		}
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		try{
			String  sortStr = str.trim().substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortStr.matches("[A-Z]")) {
				return sortStr;
			} else {
				return "#";
			}
		}catch(Exception e){
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}


}
