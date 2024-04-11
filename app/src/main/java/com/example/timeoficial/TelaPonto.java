package com.example.timeoficial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaPonto extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private TextView textViewLocation;
    private Button btnGetLocation;
    private TextView clockTextView;
    private Handler handler;
    private Runnable updateTimeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_ponto);

        ImageView imageView = findViewById(R.id.imageView10);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia a nova atividade ao clicar no ImageView
                Intent intent = new Intent(TelaPonto.this, RegistroPonto.class);
                startActivity(intent);
            }
        });


        textViewLocation = findViewById(R.id.textView2);
        btnGetLocation = findViewById(R.id.baterponto);

        ImageView sairButton = findViewById(R.id.botao_sair);



        sairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        // Inicializa o TextView do relógio
        clockTextView = findViewById(R.id.clock);

        // Inicializa o Handler
        handler = new Handler();

        // Inicializa o Runnable para atualizar o tempo
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                // Agenda o próximo update para 1 segundo no futuro
                handler.postDelayed(this, 1000);
            }
        };

        // Inicia o Runnable pela primeira vez
        handler.post(updateTimeRunnable);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja voltar para a tela anterior?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Volta para a tela anterior
                        onBackPressed();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Fecha o diálogo
                        dialog.dismiss();
                    }
                });
        // Cria e exibe o diálogo de confirmação
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void onDestroy() {
        super.onDestroy();
        // Remove o Runnable quando a atividade for destruída para evitar memory leaks
        handler.removeCallbacks(updateTimeRunnable);
    }

    private void updateTime() {
        // Obtém a hora atual
        Date date = new Date();

        // Formata a hora atual como string (HH:mm:ss)
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(date);

        // Atualiza o texto do TextView com a hora atual
        clockTextView.setText(currentTime);
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Geocoder geocoder = new Geocoder(TelaPonto.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        String streetAddress = address.getAddressLine(0);
                                        textViewLocation.setText(streetAddress);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    textViewLocation.setText("Erro ao obter o endereço");
                                }
                            } else {
                                textViewLocation.setText("Não foi possível obter a localização");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            textViewLocation.setText("Erro ao obter a localização: " + e.getMessage());
                        }
                    });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}