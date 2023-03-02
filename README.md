## Steps to recreate

1. Assume one has a GCP account and has the following secret with name `secret_txt` (see line 16 of bug/nativeCompile/googleSecretManager/ApplicationSecretsClient.java).
2. Change the `gcp.project-id` in application.yml to correct value.
3. Use GraalVM via sdk man or some other means. On my end, I enable GraalVM via `sdk use java 22.2.r17-grl` 
4. Execute `./gradlew nativeCompile`.
5. Run the generated native image `./build/native/nativeCompile/bug-nativeCompile-googleSecretManager`
6. Observe that `io.grpc.auth.GoogleAuthLibraryCallCredentials createJwtHelperOrNull
   WARNING: Failed to create JWT helper. This is unexpected
   java.lang.NoSuchMethodException: com.google.auth.oauth2.ServiceAccountCredentials.getQuotaProjectId()
   ` error is generated. The above error is not generated whenever `./gradlew run` is executed.

