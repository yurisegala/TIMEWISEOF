package com.example.timeoficial;

import android.net.Uri;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.provider.Settings;
;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_REQUEST_CODE = 1001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Verifica a disponibilidade do Google Play Services
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            // Google Play Services não está disponível ou atualizado
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                // O erro é recuperável, exibe uma mensagem para o usuário
                Toast.makeText(this, "Google Play Services não está disponível ou atualizado. Por favor, atualize o Google Play Services.", Toast.LENGTH_LONG).show();

                // Abre as configurações para que o usuário possa atualizar o Google Play Services
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                // Erro não recuperável, exibe uma mensagem e encerra o aplicativo
                Toast.makeText(this, "Google Play Services não está disponível ou atualizado. O aplicativo será encerrado.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
            // Verifica novamente a disponibilidade do Google Play Services após o retorno das configurações
            if (resultCode == RESULT_OK) {
                recreate(); // Reinicia a atividade para refletir as alterações após a atualização do Google Play Services
            } else {
                // O usuário não atualizou o Google Play Services, exibe uma mensagem e encerra o aplicativo
                Toast.makeText(this, "Google Play Services não foi atualizado. O aplicativo será encerrado.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}