package mm.mapapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    LocationManager locMan;
    String provider;
    Criteria criteria;
    TextView adressTV;
    static final int requestNumber=777;
    boolean accessGranted=true;
    Activity act=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locMan=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        criteria=new Criteria();
        adressTV=(TextView)findViewById(R.id.adressTV);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            accessGranted=false;
            Toast.makeText(this, "trzeba włączyć uprawnienia", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    requestNumber);
        }
        if(accessGranted) {
            LocationListener locListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    onResume();
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

            // teraz kontunuuj to https://developer.android.com/training/permissions/requesting.html
            //trzeba metode co zwraca wynik z tego czy uzytkownik grantował akces...

            provider = locMan.getBestProvider(criteria, false);
            locMan.requestLocationUpdates(provider,0,0, locListener);
            Location location = locMan.getLastKnownLocation(provider);
            if (location == null) { //jesli null to ustwia sztuczną pozycje na Kartagine w Kolumbii
                Location loc = new Location(provider);
                loc.setLatitude(10.24);
                loc.setLongitude(75.30);
                loc.setTime(System.currentTimeMillis());
                location = loc;
                Toast.makeText(this,"nie można było odczytać lokaluzacji, ustawiono przykłądową: Kartagina, Kolombia",Toast.LENGTH_SHORT).show();
            }
            String s = "szerokość geograficzna: "+location.getLatitude() + "\n długość geograficzna: " + location.getLongitude();
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
            case R.id.toMapOption:
                Intent intent=new Intent(this,MapsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
