package bug.nativeCompile.googleSecretManager;

import com.google.cloud.secretmanager.v1.*;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class ApplicationSecretsClient {

    public static final String SECRET_FILE_NAME = "secret_txt";

    @Property(name = "gcp.project-id")
    protected String project;
    @Property(name = "gcp.credentials.enabled")
    protected Boolean enabled;
    private String secret_content;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationSecretsClient.class);

    public ApplicationSecretsClient() {
    }

    @EventListener
    public void onStartup(StartupEvent event) {

        if (project != null && enabled != null && enabled) {
            try {
                SecretManagerServiceClient client = SecretManagerServiceClient.create();
                this.secret_content =  getLatestSecret(client, project, SECRET_FILE_NAME);
                LOG.info("Secret content is: " + this.secret_content);
                client.close();
            } catch (Exception e) {
                LOG.error("Could not get secrets from GCP: '" + e.getMessage() + "'");
                // all or nothing
                this.secret_content = null;
            }
        }
    }

    private String getLatestSecret(SecretManagerServiceClient client, String project, String file) {
        AccessSecretVersionResponse response = client.accessSecretVersion(AccessSecretVersionRequest
                .newBuilder()
                .setName(SecretVersionName.of(project, file, "latest").toString())
                .build());
        return response.getPayload().getData().toStringUtf8();
    }

    public Optional<String> getIdentityCACert() {
        return Optional.ofNullable(secret_content);
    }
}
