package br.com.techne.googleapi.admin.directory.user;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;

import br.com.techne.googleapi.admin.directory.AbstractDirectoryService;

/**
 * Implementação de cliente da Google Admin Directory API com scope "ADMIN_DIRECTORY_USER"
 * que permite a visualizar e gerenciar o provisionamento de usuários em seu domínio.
 *
 * @see {@link DirectoryScopes}
 *
 * @author Techne
 * @version 1.0
 * @since 10/06/2016
 */
public class DirectoryUserService extends AbstractDirectoryService {

  /**
   * Classe responsável pela geração de logs.
   */
  private static java.util.logging.Logger logger = Logger.getLogger(DirectoryUserService.class.getName());

  /**
   * Definição de serviço para Google Directory API (Admin SDK)
   */
  private static Directory service;

  /**
   * Obtém um serviço cliente autorizado para a Direcotry API
   */
  static {

    SCOPES = Arrays.asList(DirectoryScopes.ADMIN_DIRECTORY_USER);

    try {
      service = getDirectoryService();
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }

    logger.info("***    Directory Service com scope ADMIN_DIRECTORY_USER inicializado.    ***");
  }

  /**
   * Cria usuário na plataforma (domínio).
   *
   * @param userContent
   * @return objeto User com dados do usuário criado.
   * @throws IOException
   */
  public static User createUser(User userContent) throws IOException {
    return service.users().insert(userContent).execute();
  }

  /**
   * Utiliza o 'userKey' para retorna um usuário já provisionado na plataforma.
   *
   * @param userKey email ou id imutável do usuário.
   * @return objeto User com dados do usuário.
   * @throws IOException
   */
  public static User getUser(String userKey) throws IOException {
    return service.users().get(userKey).execute();
  }

  /**
   * Utiliza o 'userKey' para identificar o usuário provisionado na plataforma
   * e atualizá-lo com o conteúdo em 'userContent'.
   *
   * @param userKey email ou id imutável do usuário, se id, o mesmo deve estar em userContent.
   * @return objeto User com dados atualizados do usuário.
   * @throws IOException
   */
  public static User updateUser(String userKey, User userContent) throws IOException {
    return service.users().update(userKey, userContent).execute();
  }

  /**
   * Utiliza o 'userKey' para remover um usuário já provisionado da plataforma.
   *
   * @param userKey email ou id imutável do usuário.
   * @return
   * @throws IOException
   */
  public static void deleteUser(String userKey) throws IOException {
    service.users().delete(userKey).execute();
  }

  /**
   * Lista os 'maxResults' primeiros usuários provisionados na plataforma ordenados por 'orderBy'.
   *
   *<pre>
   * Obs:
   * 1. Valores permitidos orderBy: [email, familyname, givenname]
   * 2. Esta listagem é multi-domínio
   * </pre>
   *
   * @param maxResults
   * @param orderBy
   * @return List de objetos User.
   * @throws IOException
   */
  public static List<User> listUsers(int maxResults, String query, String orderBy) throws IOException {

    com.google.api.services.admin.directory.Directory.Users.List usersListService = service.users().list().setCustomer("my_customer");

    if(maxResults > 0){
      usersListService.setMaxResults(maxResults);
    }

    if(query != null && !"".equals(query)){
      usersListService.setQuery(query);
    }

    if(orderBy != null && !"".equals(orderBy)){
      usersListService.setOrderBy(orderBy);
    }

    Users result = usersListService.execute();

    return result.getUsers();
  }

  /**
   * Lista usuários do domínio ordenados por 'email'.
   *
   * @param maxResults
   * @throws IOException
   */
  public static List<User> listUsersByEmail() throws IOException {

    return listUsers(0, "", "email");
  }

  /**
   * Lista usuários do domínio ordenados por 'givenname'.
   *
   * @param maxResults
   * @throws IOException
   */
  public static List<User> listUsersByName() throws IOException {

    return listUsers(0, "", "givenname");
  }

  /**
   * Cria uma fila de chamadas ao serviço de inserção de usuário e executa apenas
   * um Http Request para todas as entradas de uma só vez.
   *
   * @param userContentList lista de objetos User com informações de usuários.
   * @throws IOException
   */
  public static void createUsers(List<User> userContentList) throws IOException {

    BatchRequest batch = service.batch();

    /*
     * Criando callback do processo batch para cada request.
     */
    JsonBatchCallback<User> callback = new JsonBatchCallback<User>() {

      public void onSuccess(User user, HttpHeaders responseHeaders) {
        logger.finest(String.format("Usuário '%s' criado com sucesso.", user.getName().getFullName()));
      }

      @Override
      public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
        logger.severe("Error Message: " + e.getMessage());
        throw new RuntimeException(e.getMessage());
      }
    };

    /*
     * Preenchendo processo batch.
     */
    for(User user : userContentList) {
      service.users().insert(user).queue(batch, callback);
    }

    batch.execute();
  }

}
