package com.ashishlakhmani.ashishtexteditor;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText text;
    Stack<String> undo_stack = new Stack<>();
    Stack<String> redo_stack = new Stack<>();

    boolean is_from_undo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ConstraintLayout constraintLayout = header.findViewById(R.id.header_layout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.github.com/ashishlkhmn48")));
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!undo_stack.isEmpty()) {
                    if (!undo_stack.peek().equals(s.toString())) {
                        undo_stack.push(s.toString());
                    }
                } else {
                    if (!s.toString().equals(""))
                        undo_stack.push(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_undo) {
            //Undo Task..
            if (!undo_stack.isEmpty()) {
                String top = undo_stack.pop();
                redo_stack.push(top);

                if (undo_stack.isEmpty()) {
                    text.setText("");
                } else {
                    text.setText(undo_stack.peek());
                }
            }
            text.setSelection(text.getText().toString().length());

            return true;
        } else if (id == R.id.action_redo) {
            //Redo Task..
            if (!redo_stack.isEmpty()) {
                text.setText(redo_stack.pop());
            }
            text.setSelection(text.getText().toString().length());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        TextEditUtil teu = new TextEditUtil(text);

        if (id == R.id.nav_display_all) {
            HashMap<Integer, String> map = teu.displayAll();
            displayTask(teu, map);
        } else if (id == R.id.nav_display_multiple_lines) {
            openAlertDialogue("Display", Operation.DISPLAY, teu);
        } else if (id == R.id.nav_insert_at_line) {
            insertTask(teu);
        } else if (id == R.id.nav_delete_single_line) {
            openAlertDialogue2("Delete", Operation.DELETE, teu);
        } else if (id == R.id.nav_delete_multiple_lines) {
            openAlertDialogue("Delete", Operation.DELETE, teu);
        } else if (id == R.id.nav_copy) {
            openAlertDialogue("Copy", Operation.COPY, teu);
        } else if (id == R.id.nav_paste) {
            openAlertDialogue2("Paste", Operation.PASTE, teu);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //To Diplay in Another Fragment
    public void displayTask(TextEditUtil teu, HashMap<Integer, String> map) {
        Intent intent = new Intent(this,DisplayActivity.class);
        intent.putExtra("map",map);
        startActivity(intent);
    }

    //To Display(m : n) , Delete(m : n) , Copy(m : n)
    public void openAlertDialogue(String title, final Operation operation, final TextEditUtil teu) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title + " from Starting Line to End Line");

        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        final TextInputEditText start = view.findViewById(R.id.start);
        final TextInputEditText end = view.findViewById(R.id.end);


        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    int start_num = Integer.parseInt(start.getText().toString());
                    int end_num = Integer.parseInt(end.getText().toString());

                    if ((start_num < end_num) && (start_num > 0) && (start_num <= text.getLineCount()) && (end_num > 0) && (end_num <= text.getLineCount())) {

                        if (operation == Operation.DISPLAY) {
                            HashMap<Integer, String> map = teu.displayMultipleLines(start_num, end_num);
                            displayTask(teu, map);
                        } else if (operation == Operation.COPY) {
                            String str = teu.copy(start_num, end_num);
                            copyTask(str);
                        } else if (operation == Operation.DELETE) {
                            teu.deleteMultipleLines(start_num, end_num);
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Please Enter Valid Values.", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please Enter Numbers Only.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

    }

    //To Delete(line m) , Paste(line m)
    public void openAlertDialogue2(String title, final Operation operation, final TextEditUtil teu) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title + " a Line");

        View view = getLayoutInflater().inflate(R.layout.layout_dialog2, null);
        final TextInputEditText line_num = view.findViewById(R.id.line_num);


        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    int line_number = Integer.parseInt(line_num.getText().toString());

                    if (line_number <= text.getLineCount() && line_number > 0) {

                        if (operation == Operation.PASTE) {
                            pasteTask(line_number, teu);
                        } else if (operation == Operation.DELETE) {
                            teu.deleteSingleLine(line_number);
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Please Enter Valid Values.", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please Enter Numbers Only.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

    }

    //To Insert at (line m)
    public void insertTask(final TextEditUtil teu) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Insert at Line");

        View view = getLayoutInflater().inflate(R.layout.layout_dialog3, null);
        final TextInputEditText line_num = view.findViewById(R.id.line_num);
        final TextInputEditText input_text = view.findViewById(R.id.input_text);


        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    int line_number = Integer.parseInt(line_num.getText().toString());
                    String input = input_text.getText().toString();

                    if (line_number <= text.getLineCount() && line_number > 0 && !input.trim().isEmpty()) {

                        teu.insertAtLine(line_number, input);
                        Toast.makeText(MainActivity.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "Please Enter Valid Values.", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Please Enter Numbers Only.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

    }

    //To Copy the String to the Clipboard
    public void copyTask(String str) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy_text", str);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Selected Lines Copied.", Toast.LENGTH_SHORT).show();
    }

    public void pasteTask(int m, TextEditUtil teu) {
        String textToPaste = null;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard.getPrimaryClip() != null) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            textToPaste = item.getText().toString();
            teu.insertAtLine(m, textToPaste);
            Toast.makeText(this, "Pasted at Line " + m + " Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing in the Clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    //To select which operation we need.
    enum Operation {
        DISPLAY, DELETE, COPY, PASTE
    }


}
