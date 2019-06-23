/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osintegration_mac;

import java.awt.Component;
import java.awt.Image;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author draque
 */
public class OSIntegration_Mac {

    public static void setDisplayName(String displayName) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", displayName);
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", displayName);
    }
    
    public static void setIcon(Image icon) {
        com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
        macApp.setDockIconImage(icon);
    }
    
    public static void setAboutHandler(Runnable aboutScreen) {
        com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
        
        macApp.setAboutHandler(new com.apple.eawt.AboutHandler() {
            @Override
            public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                aboutScreen.run();
            }
        });
    }
    
    public static Object setBlankAppleMenuBar() {
        com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
        
        // populate menu bar with placeholders until they can be populated later
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu(" "));
        menuBar.add(new JMenu("  "));
        menuBar.add(new JMenu("   "));
        menuBar.add(new JMenu("    "));
        menuBar.add(new JMenu("     "));
        macApp.setDefaultMenuBar(menuBar);
        
        return menuBar; 
    }
    
    /**
     * Takes menu bar from PolyGlot and plops it into the Apple bar
     * @param sourceMenuBar The menu bar this is pulling from (in the main display)
     * @param targeObject The target menu, which must be instantiated BEFORE nimbus is set
     * @throws ArrayIndexOutOfBoundsException If there are more menus in PolyGlot than planned 
     * for here (all have to be created prior to program instantiation)
     */
    public static void integrateMacMenuBar(JMenuBar sourceMenuBar, Object targeObject) throws ArrayIndexOutOfBoundsException {
        if (targeObject instanceof JMenuBar) {
            JMenuBar targetBar = (JMenuBar)targeObject;
            com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
            Component[] sourceComponents = sourceMenuBar.getComponents();
            Component[] targetComponents = targetBar.getComponents();

            for (int i = 0; i < targetComponents.length; i++) {
                if (i >= targetComponents.length) {
                    throw new ArrayIndexOutOfBoundsException("DEVELOPER ERROR: more menu space needed in OSIntegration_Mac module.");
                } else if (i < sourceComponents.length) {
                    JMenu targetMenu = (JMenu)targetComponents[i];
                    JMenu sourceMenu = (JMenu)sourceComponents[i];
                    
                    // cycle through each menu and pull its components
                    for (Component subComp : sourceMenu.getMenuComponents()) {
                        targetMenu.add(subComp);
                    }
                    
                    targetMenu.setText(sourceMenu.getText());
                    sourceMenuBar.remove(sourceMenu);
                } else {
                    targetComponents[i].setVisible(false);
                }
            }
            
            sourceMenuBar.setVisible(false); // no need for this once it's in the top apple bar
            macApp.setDefaultMenuBar(targetBar);
        }
    }
}
