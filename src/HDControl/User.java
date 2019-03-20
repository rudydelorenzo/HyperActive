/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HDControl;


public class User {
    
    private String username;
    private String name;
    private String password;
    private String accountType;
    
    public User(String username, String name, String password, String accountType) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.accountType = accountType;
    }
    
    public String getUsername() {
        return username;
    }
    public String getName() {
        return name;
    }
    public String getHashedPass() {
        return password;
    }
    public String getAccountType() {
        return accountType;
    }
}
