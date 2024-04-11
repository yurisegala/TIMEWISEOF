package com.example.timeoficial;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

public class TelaPonto extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private TextView textViewTime;
    private TextView textViewLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_ponto);

        textViewLocation = findViewById(R.id.textView2);
        textViewTime = findViewById(R.id.clock);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verifica se a permissão ACCESS_FINE_LOCATION já foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não foi concedida, solicita permissão ao usuário
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Se a permissão já foi concedida, inicia a atualização da localização
            updateLocation();
        }

        // Inicializa a atualização do tempo
        updateTime();

        // Inicializa a atualização da localização
        updateLocation();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopUpdateLocationAndTime();
    }


    private void stopUpdateLocationAndTime() {
        // Não há nada a parar aqui, pois as atualizações de localização e tempo são executadas apenas quando a atividade está ativa.
    }

    private void updateTime() {
        // Obtendo a hora atual
        Date date = new Date();

        // Criando um formato de hora
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Formatando a data para obter a hora atual
        String currentTime = dateFormat.format(date);

        // Exibindo a hora atual na TextView
        textViewTime.setText(currentTime);
    }

    private void updateLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(TelaPonto.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String streetAddress = address.getAddressLine(0); // Rua e número
                                    textViewLocation.setText(streetAddress);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                textViewLocation.setText("Erro ao obter o endereço");
                            }
                        } else {
                            textViewLocation.setText("Não foi possível obter a localização");
                        }

                        // Aqui você pode chamar updateLocation() novamente após definir uma nova localização
                        // updateLocation();
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, atualize a localização
                updateLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
