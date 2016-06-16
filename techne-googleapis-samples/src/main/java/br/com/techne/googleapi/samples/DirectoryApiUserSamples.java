package br.com.techne.googleapi.samples;

import java.io.IOException;
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


  public static void main(String[] args) throws IOException {

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
      System.out.println("Usuário não incluído.");
    }
    else {
      System.out.println(user.toPrettyString());
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
      System.out.println("Usuário não encontrado.");
    }
    else {
      System.out.println(user.toPrettyString());
    }
  }

  public static void updateUserSample() throws IOException {

    UserPhone phone = new UserPhone().setValue("+551121499247").setPrimary(true).setType("work");

    User userContent = new User();
    userContent.setPhones(Arrays.asList(phone));

    User user = DirectoryUserService.updateUser("jose.emanuel@gedu.demo.foreducation.com.br", userContent);

    if(user == null) {
      System.out.println("Usuário não incluído.");
    }
    else {
      System.out.println(user.toPrettyString());
    }
  }

  public static void deleteUserSample() throws IOException {
    DirectoryUserService.deleteUser("jose.emanuel@gedu.demo.foreducation.com.br");
  }

  public static void listUsersSample() throws IOException {
    System.out.println("********************** Users by email ********************** ");

    List<User> users = DirectoryUserService.listUsersByName();

    if(users == null || users.size() == 0) {
      System.out.println("Nenhum usuário encontrado.");
    }
    else {
      System.out.println("Usuários:");
      for(User user : users) {
        System.out.println(user.getName().getFullName());
      }
    }
  }

  public static void queryUsersSample() throws IOException {
    System.out.println("********************** Query users starting with jose.emanuel ********************** ");

    String query = "givenName:'Jose Emanuel*'";

    List<User> users = DirectoryUserService.listUsers(0, query, "familyName");

    if(users == null || users.size() == 0) {
      System.out.println("Nenhum usuário encontrado.");
    }
    else {
      System.out.println("Usuários:");
      for(User user : users) {
        System.out.println(user.getName().getFullName());
      }
    }
  }

}
