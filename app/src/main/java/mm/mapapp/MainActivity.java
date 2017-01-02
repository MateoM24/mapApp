package mm.mapapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    LocationManager locMan;
    String provider;
    Criteria criteria;
    EditText nameTV;
    EditText descTV;
    TextView adressTV;
    static final int requestNumber=777;
    boolean accessGranted=true;
    LocationListener locListener;
    Location currentPosition;
    String nazwa;
    String opis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locMan=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        criteria=new Criteria();
        nameTV=(EditText)findViewById(R.id.nameTV);
        descTV=(EditText)findViewById(R.id.descTV);
        nazwa="dodaj nazwę miejsca";
        opis="dodaj opis";
        nameTV.setText(nazwa);
        descTV.setText(opis);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((EditText)view).getText().toString()==nazwa||((EditText)view).getText().toString()==opis){
                    ((EditText) view).setText("");
                }
            }
        };
        nameTV.setOnClickListener(onClickListener);
        descTV.setOnClickListener(onClickListener);
        adressTV=(TextView)findViewById(R.id.adressTV);
        locListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentPosition=location;
                updatePositionView();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }
    public void updatePositionView(){
        String s = "szerokość geograficzna: "+currentPosition.getLatitude() + "\n długość geograficzna: " + currentPosition.getLongitude();
        adressTV.setText(s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nazwa="dodaj nazwę miejsca";
        opis="dodaj opis";
        nameTV.setText(nazwa);
        descTV.setText(opis);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            accessGranted=false;
            Toast.makeText(this, "trzeba włączyć uprawnienia", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    requestNumber);
        }
        if(accessGranted) {
            //https://developer.android.com/training/permissions/requesting.html
            provider = locMan.getBestProvider(criteria, false);
            locMan.requestLocationUpdates(provider,0,0, locListener);
            currentPosition=locMan.getLastKnownLocation(provider);
            if (currentPosition == null) { //jesli null to ustawia sztuczną pozycje na Kartagine w Kolumbii
                Location loc = new Location(provider);
                loc.setLatitude(10.24);
                loc.setLongitude(75.30);
                loc.setTime(System.currentTimeMillis());
                currentPosition = loc;
                Toast.makeText(this,"nie można było odczytać lokalizacji, ustawiono przykłądową: Kartagina, Kolumbia",Toast.LENGTH_SHORT).show();
            }
            String s = "szerokość geograficzna: "+currentPosition.getLatitude() + "\n długość geograficzna: " + currentPosition.getLongitude();
            adressTV.setText(s);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case requestNumber:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED){
                          accessGranted=true;  //access granted
                }else{
                    accessGranted=false; //access denied
                    Toast.makeText(this,"brak uprawnień potrzebnych do działania",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toMap:
                Intent intent=new Intent(this,MapsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void addPlace(View v){
        String newName=nameTV.getText().toString();
        String newDesc=descTV.getText().toString();
        if (newName==nazwa||newDesc==opis){
            Toast.makeText(this,"dodaj nazwę i opis",Toast.LENGTH_LONG).show();
        }else{
            DBHelper dbHelper=new DBHelper(this);
            try {
                DBHelper.insert(dbHelper.getWritableDatabase(), newName, newDesc, currentPosition.getLatitude(), currentPosition.getLongitude());
            }catch (SQLiteConstraintException e){
                Toast.makeText(this,"To miejsce jest już na zaznaczone na mapie",Toast.LENGTH_LONG).show();
            }
        }
        Intent intent=new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
}
