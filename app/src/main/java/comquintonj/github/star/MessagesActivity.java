package comquintonj.github.star;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    /**
     * Past conversations
     */
    private ArrayList<String> pastSenders = new ArrayList<>();

    /**
     * EditText used to create a new conversation
     */
    private EditText userInput;

    /**
     * The key to access the sender of the message
     */
    static String KEY_SENDER = "Sender";

    /**
     * The key to access the text of the Message
     */
    static String KEY_MESSAGE = "Message";

    /**
     * SQLite database for messages
     */
    private MessagesSQLiteHelper messagesDBHelp;

    /**
     * Adapter use to populate the ListView
     */
    private MessageListAdapter messageAdapter;

    /**
     * Holds the text of the new sender to be added
     */
    private String newMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        final Context context = this;

        messagesDBHelp = new MessagesSQLiteHelper(this);

        // Retrieve past messages from the SQLite database
        pastSenders = messagesDBHelp.getSenders();

        // Create adapter
        messageAdapter = new MessageListAdapter(this, pastSenders);

        // Populate adapter
        final ListView messageView = (ListView) findViewById(R.id.message_list);
        messageView.setAdapter(messageAdapter);

        messageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent messageIntent = new Intent(MessagesActivity.this, MainActivity.class);
                String selected = pastSenders.get(position);
                messageIntent.putExtra("Sender", selected);
                startActivity(messageIntent);
            }
        });

        messageView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                String selected = pastSenders.get(position);
                messagesDBHelp.clearAllPastMessages(messagesDBHelp, selected);
                recreate();
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabButton);
        DrawableCompat.setTint(fab.getDrawable(), ContextCompat.getColor(this, R.color.white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Set up the input for a new conversation
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input, null);
                userInput = (EditText) mView.findViewById(R.id.user_input_text);
                builder.setView(mView);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newMessage = userInput.getText().toString();
                        Intent mainIntent = new Intent(MessagesActivity.this, MainActivity.class);
                        mainIntent.putExtra("Sender", newMessage);
                        messagesDBHelp.addMessage(newMessage, "First", "First");
                        startActivity(mainIntent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    /**
     * Create the options menu found in the top right of the activity
     * @param menu the menu to be added
     * @return if the menu has been successfully added
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }
}
