package br.com.techne.googleapi.samples;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;
import com.google.api.services.admin.directory.model.UserPhone;

import br.com.techne.googleapi.admin.directory.user.DirectoryUserService;

/**
 * Centraliza chamadas aos métodos de exemplos de uso do Directory API Service.
 *
 * @author Techne
 * @version 1.0
 * @since 15/06/2016
 */
public class DirectoryApiUserSamples {

  private static final PrintStream log = System.out;

  private static final String HELP_MSG =
          "Uso: java -jar techne-googleapis-samples-0.0.1-SNAPSHOT-jar-with-dependencies.jar [-options] \n\n" +
          "Onde 'options' inclui:\n\n" +
          "-authenticate        realiza o OAuth2 Authentication Code Flow\n" +
          "                     (necessário quando se deseja utilizar a api em processos agendados e/ou em lote).\n\n" +
          "-run                 faz uso da Directory API como implementado.\n" +
          "                     (realiza o OAuth2 Authentication Code Flow se necessário).\n\n" +
          "-help                mostra este texto de ajuda.\n\n";

  public static void main(String[] args) throws IOException {

    if(args != null && args.length == 1 && "-authenticate".equals((String)args[0])) {
      log.println("***    Inicializando autenticação OAuth 2.0    ***");
      DirectoryUserService.authorize();
      System.exit(0);
    }

    if(args != null && args.length == 1 && "-run".equals((String)args[0])) {
      log.println("***    Executando Sample    ***");
      run();
      System.exit(0);
    }

    log.println(HELP_MSG);
    System.exit(-1);
  }

  public static void run() throws IOException {
    /*
     * Descomente o método(s) para testar.
     */

    // Exemplo de criação de usuário
    //createUserSample();

    // Exemplo de criação de lista de usuários
    //createUsersSample();

    // Exemplo de obtenção de usuário
    getUserSample();

    // Exemplo de atualização de usuário
    //updateUserSample();

    // Exemplo de remoção de usuário
    //deleteUserSample();

    // Exemplo listagem de usuários
    //listUsersSample();

    // Exemplo listagem de usuários por givenName query
    //queryUsersSample();
  }

  public static void createUserSample() throws IOException {
    User userContent = new User();

    UserName userName = new UserName().setGivenName("Jose").setFamilyName("Emanuel");

    userContent.setName(userName);
    userContent.setPrimaryEmail("jose.emanuel@gedu.demo.foreducation.com.br");
    userContent.setPassword("password");
    userContent.setChangePasswordAtNextLogin(true);
    userContent.setOrgUnitPath("/Techne");

    User user = DirectoryUserService.createUser(userContent);

    if(user == null) {
      log.println("Usuário não incluído.");
    }
    else {
      log.println(user.toPrettyString());
    }
  }

  public static void createUsersSample() throws IOException {

    ArrayList<User> userList = new ArrayList<User>();

    User userContent;
    UserName userName;
    String numericVar;

    for(int i = 0; i < 10; i++) {
      numericVar = "Num" + i;
      userName = new UserName().setGivenName("Jose Emanuel").setFamilyName(numericVar);

      userContent = new User();

      userContent.setName(userName);
      userContent.setPrimaryEmail("jose.emanuel."+ numericVar +"@gedu.demo.foreducation.com.br");
      userContent.setPassword("password");
      userContent.setChangePasswordAtNextLogin(true);
      userContent.setOrgUnitPath("/Techne");

      userList.add(userContent);
    }

    DirectoryUserService.createUsers(userList);
  }

  public static void getUserSample() throws IOException {

    User user = DirectoryUserService.getUser("techne@gedu.demo.foreducation.com.br");

    if(user == null) {
      log.println("Usuário não encontrado.");
    }
    else {
      log.println(user.toPrettyString());
    }
  }

  public static void updateUserSample() throws IOException {

    log.println("********************** Update User with phone number ********************** ");

    UserPhone phone = new UserPhone().setValue("+551121499247").setPrimary(true).setType("work");

    User userContent = new User();
    userContent.setPhones(Arrays.asList(phone));

    User user = DirectoryUserService.updateUser("jose.emanuel@gedu.demo.foreducation.com.br", userContent);

    if(user == null) {
      log.println("Usuário não incluído.");
    }
    else {
      log.println(user.toPrettyString());
    }
  }

  public static void deleteUserSample() throws IOException {
    DirectoryUserService.deleteUser("jose.emanuel@gedu.demo.foreducation.com.br");
  }

  public static void listUsersSample() throws IOException {
    log.println("********************** List Users by email ********************** ");

    List<User> users = DirectoryUserService.listUsersByName();

    if(users == null || users.size() == 0) {
      log.println("Nenhum usuário encontrado.");
    }
    else {
      log.println("Usuários:");
      for(User user : users) {
        log.println(user.getName().getFullName());
      }
    }
  }

  public static void queryUsersSample() throws IOException {
    log.println("********************** Query users starting with jose.emanuel ********************** ");

    String query = "givenName:'Jose Emanuel*'";

    List<User> users = DirectoryUserService.listUsers(0, query, "familyName");

    if(users == null || users.size() == 0) {
      log.println("Nenhum usuário encontrado.");
    }
    else {
      log.println("Usuários:");
      for(User user : users) {
        log.println(user.getName().getFullName());
      }
    }
  }

}
