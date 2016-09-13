package com.example.khacc.googlemapdemo15082016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by khacc on 18-Aug-16.
 */
public class ListAdapter extends ArrayAdapter<String> {

    private DatabaseHelper db;
    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    public ListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.type_listview, dataItem);
        this.data = dataItem;
        this.context = context;
        this.db = new DatabaseHelper(context);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.type_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView
                    .findViewById(R.id.childTextView);
            viewHolder.textDate = (TextView) convertView.findViewById(R.id.textViewDate);
            viewHolder.button = (Button) convertView
                    .findViewById(R.id.childButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        if (temp.length() > 20 ) {
            String[] route = temp.split(Pattern.quote("."));
            viewHolder.text.setText(route[0]);
            viewHolder.textDate.setText(route[1]);
            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = viewHolder.text.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("name_route", value);
                    Intent intent = new Intent(context, ShowMapActivity.class);
                    intent.putExtra("Package", bundle);
                    context.startActivity(intent);
                }
            });

            viewHolder.button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Xác nhận");
                    alertDialog.setMessage("Bạn có muốn xóa không?");

                    alertDialog.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String value = viewHolder.text.getText().toString();
                                    Integer res = db.deleteData(value);
                                    if (res > 0) {
                                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                        data.remove(position);
                                        notifyDataSetChanged();
                                    } else
                                        Toast.makeText(context, "Delete fail", Toast.LENGTH_SHORT).show();
                                }
                            });

                    alertDialog.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                }
            });
        }
        else {
            viewHolder.button.setBackground(null);
        }

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        TextView textDate;
        Button button;
    }
}