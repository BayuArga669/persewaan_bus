/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package antasena3;
import view.FormSplashScreen;
import view.FormLogin;

/**
 *
 * @author bayu
 */
public class Antasena3 {

    public static void main(String[] args) {
        // Start the application with login form
        java.awt.EventQueue.invokeLater(() -> {
            FormSplashScreen splash = new FormSplashScreen();
            splash.startLoading();
        });
    }
}
