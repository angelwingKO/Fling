package com.angelwing.fling;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    MainActivity thisActivity = this;
    SharedPreferences sp;
    EditText firstRecipientField;
    LinearLayout additionalRecipients;
    LinearLayout additionalInformation;
    String userName;
    Menu menu;

    String toEmail;
    String toPhone;

    ArrayList<String> recipients;
    ArrayList<String> phoneRecipients;
    ArrayList<String> emailRecipients;

    Set<String> myPhoneNumbers;
    Set<String> myEmailAddresses;
    Set<String> myFacebookHandles;
    Set<String> myInstagramHandles;
    Set<String> myTwitterHandles;
    ArrayList<Character> infoTypes;
    int numRecipients;
    int numInformation;

    final int MY_PERMISSIONS_SEND_SMS = 22222;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("com.angelwing.fling", Context.MODE_PRIVATE);

        userName = sp.getString("userName", "");
        if (userName.equals(""))
        {
            openNameDialog();
        }
        additionalRecipients = (LinearLayout) findViewById(R.id.newRecipientInsert);
        additionalInformation = (LinearLayout) findViewById(R.id.newInfoInsert);
        infoTypes = new ArrayList<>();

        recipients = new ArrayList<>();
        phoneRecipients = new ArrayList<>();
        emailRecipients = new ArrayList<>();
        numRecipients = 0;

        Set<String> myNumbers = sp.getStringSet("myPhoneNumbers", new HashSet<String>());
        Set<String> myEmails = sp.getStringSet("myEmailAddresses", new HashSet<String>());
        Set<String> myFacebooks = sp.getStringSet("myFacebookHandles", new HashSet<String>());
        Set<String> myInstagrams = sp.getStringSet("myInstagramHandles", new HashSet<String>());
        Set<String> myTwitters = sp.getStringSet("myTwitterHandles", new HashSet<String>());

        myPhoneNumbers = new HashSet<>(myNumbers);
        myEmailAddresses = new HashSet<>(myEmails);
        myFacebookHandles = new HashSet<>(myFacebooks);
        myInstagramHandles = new HashSet<>(myInstagrams);
        myTwitterHandles = new HashSet<>(myTwitters);

        int i;
        for (String twitterHandle : myTwitterHandles)
        {
            View informationField = getLayoutInflater().inflate(R.layout.field_information, null);
            TextView infoTypeField = (TextView) informationField.findViewById(R.id.infoTypeField);
            TextView infoField = (TextView) informationField.findViewById(R.id.infoField);

            infoTypeField.setText("Twitter");
            infoField.setText(twitterHandle);

            setInfoDelete(informationField);

            additionalInformation.addView(informationField);
            infoTypes.add('c');
        }
        for (String instagramHandle : myInstagramHandles)
        {
            View informationField = getLayoutInflater().inflate(R.layout.field_information, null);
            TextView infoTypeField = (TextView) informationField.findViewById(R.id.infoTypeField);
            TextView infoField = (TextView) informationField.findViewById(R.id.infoField);

            infoTypeField.setText("Instagram");
            infoField.setText(instagramHandle);

            setInfoDelete(informationField);

            additionalInformation.addView(informationField);
            infoTypes.add('i');
        }
        for (String facebookHandle : myFacebookHandles)
        {
            View informationField = getLayoutInflater().inflate(R.layout.field_information, null);
            TextView infoTypeField = (TextView) informationField.findViewById(R.id.infoTypeField);
            TextView infoField = (TextView) informationField.findViewById(R.id.infoField);

            infoTypeField.setText("Facebook");
            infoField.setText(facebookHandle);

            setInfoDelete(informationField);

            additionalInformation.addView(informationField);
            infoTypes.add('f');
        }
        for (String emailAddress : myEmailAddresses)
        {
            View informationField = getLayoutInflater().inflate(R.layout.field_information, null);
            TextView infoTypeField = (TextView) informationField.findViewById(R.id.infoTypeField);
            TextView infoField = (TextView) informationField.findViewById(R.id.infoField);

            infoTypeField.setText("Email Address");
            infoField.setText(emailAddress);

            setInfoDelete(informationField);

            additionalInformation.addView(informationField);
            infoTypes.add('e');
        }
        for (String phoneNumber : myPhoneNumbers)
        {
            View informationField = getLayoutInflater().inflate(R.layout.field_information, null);
            TextView infoTypeField = (TextView) informationField.findViewById(R.id.infoTypeField);
            TextView infoField = (TextView) informationField.findViewById(R.id.infoField);

            infoTypeField.setText("Phone Number");
            infoField.setText(phoneNumber);

            setInfoDelete(informationField);

            additionalInformation.addView(informationField);
            infoTypes.add('p');
        }

        numInformation = myPhoneNumbers.size() + myEmailAddresses.size() + myFacebookHandles.size() + myInstagramHandles.size() + myTwitterHandles.size();
    }

    public void addRecipient (View view)
    {
        AlertDialog.Builder addRecipientDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_recipient, null);
        final Spinner infoTypeSpinner = (Spinner) dialogView.findViewById(R.id.infoTypeSpinner);
        final EditText infoField = (EditText) dialogView.findViewById(R.id.infoField);

        addRecipientDialogBuilder
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        infoTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0)
                    infoField.setInputType(InputType.TYPE_CLASS_PHONE);
                else
                    infoField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final AlertDialog addRecipientDialog = addRecipientDialogBuilder.create();
        addRecipientDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                addRecipientDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String info = infoField.getText().toString();

                        // Phone number
                        if (infoTypeSpinner.getSelectedItemPosition() == 0)
                        {
                            if (isPhoneNumberValid(info))
                            {
                                View recipientField = getLayoutInflater().inflate(R.layout.field_recipient, null);
                                TextView recipientText = (TextView) recipientField.findViewById(R.id.recipientField);
                                recipientText.setText(info);
                                additionalRecipients.addView(recipientField);
                                numRecipients++;

                                dialog.dismiss();
                            }
                            else
                                showErrorDialog("Please enter a valid number.");
                        }
                        // Email
                        else if (infoTypeSpinner.getSelectedItemPosition() == 1)
                        {
                            if (isEmailValid(info))
                            {
                                View recipientField = getLayoutInflater().inflate(R.layout.field_recipient, null);
                                TextView recipientText = (TextView) recipientField.findViewById(R.id.recipientField);
                                recipientText.setText(info);
                                additionalRecipients.addView(recipientField);
                                numRecipients++;

                                emailRecipients.add(info);

                                dialog.dismiss();
                            }
                            else
                                showErrorDialog("Please enter a valid email address.");
                        }

                    }
                });
            }
        });

        addRecipientDialog.show();
    }

    public void removeRecipient (View view)
    {
        additionalRecipients.removeView((ViewGroup) view.getParent());
        numRecipients--;
    }

    public void addInfo (View view)
    {
        final View informationField = getLayoutInflater().inflate(R.layout.field_information, null);

        AlertDialog.Builder addInfoDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_info, null);
        final Spinner infoTypeSpinner = (Spinner) dialogView.findViewById(R.id.infoTypeSpinner);
        final EditText infoField = (EditText) dialogView.findViewById(R.id.infoField);

        final TextView infoText = (TextView) dialogView.findViewById(R.id.infoText);
        final TextView atText = (TextView) dialogView.findViewById(R.id.atText);
        infoText.setVisibility(View.GONE);
        atText.setVisibility(View.GONE);

        infoTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0 || position == 1)
                {
                    infoText.setVisibility(View.GONE);
                    atText.setVisibility(View.GONE);

                    if (position == 0)
                    {
                        infoField.setHint("1 (555) 555-5555");
                        infoField.setInputType(InputType.TYPE_CLASS_PHONE);
                    }
                    else
                    {
                        infoField.setHint("jane@doe.com");
                        infoField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    }
                }
                else if (position == 2)
                {
                    atText.setVisibility(View.GONE);
                    infoText.setVisibility(View.VISIBLE);
                    infoText.setText("facebook.com/");

                    infoField.setInputType(InputType.TYPE_CLASS_TEXT);
                    infoField.setHint("jane.doe33");
                }
                else if (position == 3 || position == 4)
                {
                    infoText.setVisibility(View.GONE);
                    atText.setVisibility(View.VISIBLE);

                    infoField.setInputType(InputType.TYPE_CLASS_TEXT);
                    if (position == 3)
                        infoField.setHint("johnsmith55");
                    else
                        infoField.setHint("john_ira");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addInfoDialogBuilder
                .setView(dialogView)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null);

        final AlertDialog addInfoDialog = addInfoDialogBuilder.create();
        addInfoDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int infoType = infoTypeSpinner.getSelectedItemPosition();
                        String info = infoField.getText().toString();

                        if (checkNewInfo(infoType, info))
                        {
                            TextView infoTypeField = (TextView) informationField.findViewById(R.id.infoTypeField);
                            TextView infoField = (TextView) informationField.findViewById(R.id.infoField);

                            infoTypeField.setText(infoTypeSpinner.getSelectedItem().toString());
                            infoField.setText(formatInfo(infoType, info));

                            if (infoType == 0)
                            {
                                myPhoneNumbers.add(info);
                                sp.edit().putStringSet("myPhoneNumbers", myPhoneNumbers).apply();
                                infoTypes.add('p');
                            }
                            else if (infoType == 1)
                            {
                                myEmailAddresses.add(info);
                                sp.edit().putStringSet("myEmailAddresses", myEmailAddresses).apply();
                                infoTypes.add('e');
                            }
                            else if (infoType == 2)
                            {
                                myFacebookHandles.add(info);
                                sp.edit().putStringSet("myFacebookHandles", myFacebookHandles).apply();
                                infoTypes.add('f');
                            }
                            else if (infoType == 3)
                            {
                                myInstagramHandles.add(info);
                                sp.edit().putStringSet("myInstagramHandles", myInstagramHandles).apply();
                                infoTypes.add('i');
                            }
                            else if (infoType == 4)
                            {
                                myTwitterHandles.add(info);
                                sp.edit().putStringSet("myTwitterHandles", myTwitterHandles).apply();
                                infoTypes.add('t');
                            }

                            dialog.dismiss();

                            additionalInformation.addView(informationField);
                        }
                        else
                        {
                            showErrorDialog("Please fill out correctly.");
                        }
                    }
                });
            }
        });

        addInfoDialog.show();

        setInfoDelete(informationField);
    }

    public void setInfoDelete(final View informationField)
    {
        informationField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(thisActivity);
                deleteDialog
                        .setTitle("Delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int position = additionalInformation.indexOfChild(informationField);
                                additionalInformation.removeView(informationField);

                                char infoType = infoTypes.get(position);
                                String info = ((TextView) informationField.findViewById(R.id.infoField)).getText().toString();
                                if (infoType == 'p')
                                {
                                    myPhoneNumbers.remove(info);
                                    sp.edit().putStringSet("myPhoneNumbers", myPhoneNumbers).apply();
                                }
                                else if (infoType == 'e')
                                {
                                    myEmailAddresses.remove(info);
                                    sp.edit().putStringSet("myEmailAddresses", myEmailAddresses).apply();
                                }
                                else if (infoType == 'f')
                                {
                                    myFacebookHandles.remove(info);
                                    sp.edit().putStringSet("myFacebookHandles", myFacebookHandles).apply();
                                }
                                else if (infoType == 'i')
                                {
                                    myInstagramHandles.remove(info);
                                    sp.edit().putStringSet("myInstagramHandles", myInstagramHandles).apply();
                                }
                                else if (infoType == 't')
                                {
                                    myTwitterHandles.remove(info);
                                    sp.edit().putStringSet("myTwitterHandles", myTwitterHandles).apply();
                                }
                            }
                        })
                        .setNegativeButton("no", null);
                deleteDialog.show();

                return true;
            }
        });
    }

    public boolean checkNewInfo(int infoType, String info)
    {
        switch (infoType)
        {
            // Phone
            case 0:
                return isPhoneNumberValid(info) && !myPhoneNumbers.contains(info);
            // Email
            case 1:
                return isEmailValid(info) && !myEmailAddresses.contains(info);
            // Facebook
            case 2:
                return info.length() != 0 && myFacebookHandles.contains(info);
            // Instagram
            case 3:
                return info.length() != 0 && myInstagramHandles.contains(info);
            // Twitter
            case 4:
                return info.length() != 0 && myTwitterHandles.contains(info);
        }

        return true;
    }

    public String formatInfo(int infoType, String info)
    {
        return info;
    }

    public boolean isPhoneNumberValid(String number)
    {
        return !TextUtils.isEmpty(number) && Patterns.PHONE.matcher(number).matches();
    }

    public boolean isEmailValid(String email)
    {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public int getNumInfoChecked()
    {
        int numInfoChecked = 0;
        for (int i = 0; i < additionalInformation.getChildCount(); i++)
        {
            if (((CheckBox) additionalInformation.getChildAt(i).findViewById(R.id.selectBox)).isChecked())
                numInfoChecked++;
        }

        return numInfoChecked;
    }

    public void showErrorDialog(String message)
    {
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);

        errorDialog
                .setTitle("Error")
                .setMessage(message);
        errorDialog.setPositiveButton("Ok", null);
        errorDialog.show();
    }

    public void openNameDialog()
    {
        AlertDialog.Builder nameDialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_name, null);
        final EditText nameField = (EditText) dialogView.findViewById(R.id.nameField);
        nameField.setText(userName);
        nameDialogBuilder
                .setView(dialogView)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        userName = nameField.getText().toString();
                        sp.edit().putString("userName", userName).apply();
                        if (menu != null)
                        {
                            if (!userName.equals(""))
                                menu.getItem(0).setTitle(userName);
                            else
                                menu.getItem(0).setTitle("Name");
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        nameDialogBuilder.show();
    }

    public void fling (View view)
    {
        if (numRecipients == 0)
            showErrorDialog("Please enter a recipient.");
        else if (getNumInfoChecked() == 0)
            showErrorDialog("Please check info.");
        else if (userName.equals(""))
            showErrorDialog("Please edit your name.");
        else
        {
            /////////// Send

            SmsManager smsManager = SmsManager.getDefault();
            // If send sms permission hasn't been granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
            {
                String flingText = "Fling from " + userName + "\n";
                //////// Check what info you're sending
                for (String phoneNumber : myPhoneNumbers)
                {
                    flingText += phoneNumber + "\n";
                }
                for (String emailAddress : myEmailAddresses)
                {
                    flingText += emailAddress + "\n";
                }
                for (String facebookHandle : myFacebookHandles)
                {
                    flingText += "Facebook: https://www.facebook.com/" + facebookHandle + "\n";
                }
                for (String instagramHandle: myInstagramHandles)
                {
                    flingText += "Instagram: @" + instagramHandle + "\n";
                }
                for (String twitterHandle : myTwitterHandles)
                {
                    flingText += "Twitter: @" + twitterHandle;
                }

                for (String phoneNumber : phoneRecipients)
                {
                    smsManager.sendTextMessage(phoneNumber, null, flingText, null, null);
                }

                if (emailRecipients.size() != 0)
                {
                    //////// Make it BBC

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fling from " + userName);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, flingText);
                    String[] emailAddresses = Arrays.copyOf(emailRecipients.toArray(), emailRecipients.size(), String[].class);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
                    startActivity(emailIntent);
                }

                Log.i("Flingg", "Permission granted");
            }
            else
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, MY_PERMISSIONS_SEND_SMS);

            Log.i("Flingg", "Sendd");
        }
    }

    //* [ Menu ] *//
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;

        userName = sp.getString("userName", "");
        if (menu != null)
        {
            if (userName.equals(""))
                menu.getItem(0).setTitle("Name");
            else
                menu.getItem(0).setTitle(userName);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        if (item.getItemId() == R.id.name)
        {
            openNameDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
