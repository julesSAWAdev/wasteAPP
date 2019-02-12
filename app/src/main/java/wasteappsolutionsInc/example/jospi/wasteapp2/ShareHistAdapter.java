package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ShareHistAdapter extends BaseAdapter {

    Context context;
    List<ShareHistoryList> shareHistoryLists;

    public ShareHistAdapter(Context context, List<ShareHistoryList> shareHistoryLists) {
        this.context = context;
        this.shareHistoryLists = shareHistoryLists;
    }

    @Override
    public int getCount() {
        return this.shareHistoryLists.size();
    }

    @Override
    public Object getItem(int i) {
        return this.shareHistoryLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ShareItem shareItem = null;

        if (view == null){

            shareItem = new ShareItem();
            LayoutInflater li = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            assert li != null;
            view = li.inflate(R.layout.sharehistitems, null);

            shareItem.ID = view.findViewById(R.id.shareID);
            shareItem.SENDER = view.findViewById(R.id.sender);
            shareItem.RECEIVER = view.findViewById(R.id.receiver);
            shareItem.CREDIT = view.findViewById(R.id.credit);
            shareItem.DATE = view.findViewById(R.id.date);
            view.setTag(shareItem);

        }else{

            shareItem = (ShareItem) view.getTag();
        }

        shareItem.ID.setText(shareHistoryLists.get(i).ID);
        shareItem.SENDER.setText(shareHistoryLists.get(i).SENDER);
        shareItem.RECEIVER.setText(shareHistoryLists.get(i).RECEIVER);
        shareItem.CREDIT.setText(shareHistoryLists.get(i).CREDIT);
        shareItem.DATE.setText(shareHistoryLists.get(i).DATE);

        return view;
    }
}

class ShareItem
{
    TextView ID;
    TextView SENDER;
    TextView RECEIVER;
    TextView CREDIT;
    TextView DATE;

}
