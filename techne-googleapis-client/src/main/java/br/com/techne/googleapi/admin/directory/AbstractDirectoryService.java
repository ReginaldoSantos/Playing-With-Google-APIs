package br.com.techne.googleapi.admin.directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;

/**
 * Abstração de cliente da Google Admin Directory API.
 *
 * Permite que as implementações inicializem os 'scopes' desejados e obtenham
 * um serviço {@link com.google.api.services.admin.directory.Directory} com a
 * autorização necessária.
 *
 * @see {@link DirectoryScopes}
 *
 * @see https://developers.google.com/admin-sdk/directory
 *
 * @author Techne
 * @version 1.0
 * @since 14/06/2016
 */
public abstract class AbstractDirectoryService {

  /**
   * Classe responsável pela geração de logs.
   */
  private static java.util.logging.Logger logger = Logger.getLogger(AbstractDirectoryService.class.getName());

  /**
   * Nome da Aplicação.
   */
  private static final String APPLICATION_NAME = "Techne - Google Admin SDK";

  /**
   * Diretório para guardar as "user credentials" para esta aplicação.
   */
  private static final java.io.File DATA_STORE_DIR =
        new java.io.File(System.getProperty("user.home"),
                           ".credentials/techne-google-admin-sdk-v1.json");

  /**
   * Instancia global de {@link FileDataStoreFactory}.
   */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  /**
   * Instancia global de JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /**
   * Instancia global de HTTP transport.
   */
  private static HttpTransport HTTP_TRANSPORT;

  /**
   * Instancia global dos scopes requeridos pela implementação do
   * serviço e que deve ser inicializado pelo mesmo.
   *
   * Quando modificado os scopes as credencias anteriores devem
   * ser removidas de <code>DATA_STORE_DIR</code>.
   */
  protected static List<String> SCOPES = new ArrayList<String>();

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    }
    catch(Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Cria um objeto Credential autorizado utilizando o cadastro
   * da aplicação na Google Cloud Plataform.
   *
   * @return credencial autorizada.
   * @throws IOException
   */
  protected static Credential authorize() throws IOException {

    if(SCOPES == null || SCOPES.isEmpty()){
      throw new RuntimeException("Lista de 'scopes' está vazia.");
    }

    /*
     * Carregamento das credencias da aplicação criada no Google Cloud Plataform
     */
    InputStream in = AbstractDirectoryService.class.getResourceAsStream("/client_secret.json");

    if(in == null){
      in = getResourceAsStreamFromFileSystem();
    }

    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    /*
     * Constrói o code flow e faz um 'authorization request'
     */
    GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow
              .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                  .setAccessType("offline")
                    .build();

    Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    logger.info(String.format("Credenciais salvas em %s",  DATA_STORE_DIR.getAbsolutePath()));

    return credential;
  }

  /**
   * Busca pelo arquivo client_secret.json no diretório corrente ou no
   * diretório informado pela variável ambiente CLIENT_SECRET_JSON_PATH.
   *
   * Se encontrado um inputStream é devolvido, caso contrário lança uma
   * RuntimeException.
   *
   * @return client_secret.json inputStream
   */
  private static InputStream getResourceAsStreamFromFileSystem() {
    String filePath = System.getenv("CLIENT_SECRET_JSON_PATH");

    if(filePath == null || "".equals(filePath)){
      filePath = System.getProperty("user.dir");
    }

    File file = new File(filePath  + File.separator + "client_secret.json" );

    if (file == null || !file.exists()){
      logger.info("Para detalhes de como criar as credencias da aplicação veja https://support.google.com/cloud/answer/6158849");
      throw new RuntimeException("Arquivo client_secret.json não encontrado.");
    }

    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
    }
    catch(FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    return fis;
  }

  /**
   * Constrói e retorna um serviço cliente autorizado para a Admin SDK Directory.
   *
   * @return Directory client service autorizado
   * @throws IOException
   */
  protected static Directory getDirectoryService() throws IOException {

    Credential credential = authorize();

    return new Directory
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
              .setApplicationName(APPLICATION_NAME)
                .build();
  }

  /**
   * Constrói e retorna um serviço cliente autorizado com as credencias passadas por
   * parâmetro a Admin SDK Directory.
   *
   * Este método pode ser utilizado quando o uso da API não for por linha de comando.
   *
   * @return Directory client service autorizado
   * @throws IOException
   */
  protected static Directory getDirectoryService(Credential credential) throws IOException {

    return new Directory
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
              .setApplicationName(APPLICATION_NAME)
                .build();
  }

}
