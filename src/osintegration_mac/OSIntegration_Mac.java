/*
 * Copyright (c) 2019, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: Creative Commons Attribution-NonCommercial 4.0 International Public License
 * See LICENSE.TXT included with this code to read the full license agreement.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package osintegration_mac;

import com.apple.eawt.Application;
import java.awt.Component;
import java.awt.Image;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author draque
 */
public class OSIntegration_Mac {

    /**
     * Sets display name of program
     * NOTE: This must be done BEFORE any other calls to System, BEFORE UI managers
     * such as Nimbus are initialized, and BEFORE any swing objects are instantiated
     * @param displayName name to display
     */
    public static void setDisplayName(String displayName) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", displayName);
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", displayName);
    }
    
    public static void setIcon(Image icon) {
        Application macApp = Application.getApplication();
        macApp.setDockIconImage(icon);
    }
    
    public static void setPreferanceManager(Runnable prefs) {
        com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
        
        macApp.setPreferencesHandler(new PreferencesHandler() {
            @Override
            public void handlePreferences(PreferencesEvent e) {
                prefs.run();
            }
        });
    }
    
    public static void setAboutHandler(Runnable aboutScreen) {
        com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
        
        macApp.setAboutHandler(new AboutHandler() {
            @Override
            public void handleAbout(AboutEvent e) {
                aboutScreen.run();
            }
        });
    }
    
    public static void setQuitAction(Runnable quitAction) {
        com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
        
        macApp.setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
                quitAction.run();
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
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    new JFrame().setVisible(false);
                }
            });
        }
    }
}
