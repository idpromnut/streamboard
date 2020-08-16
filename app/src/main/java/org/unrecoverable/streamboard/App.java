package org.unrecoverable.streamboard;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.unrecoverable.streamboard.config.PathAndLoggingConfig;
import org.unrecoverable.streamboard.config.SerialLibraryConfiguration;

import net.twasi.obsremotejava.OBSRemoteController;
import net.twasi.obsremotejava.callbacks.Callback;
import net.twasi.obsremotejava.events.responses.SwitchScenesResponse;
import net.twasi.obsremotejava.requests.ResponseBase;
import net.twasi.obsremotejava.requests.GetVersion.GetVersionResponse;
import net.twasi.obsremotejava.requests.SetCurrentScene.SetCurrentSceneResponse;

public class App
{

	public static final String PREF_LAST_LOAD_SAVE_DIRECTORY = "lastLoadSaveDirectory";
	public static final String PREF_LAST_LOG_DIRECTORY = "lastLogDirectory";

	public static final String PREF_OPEN_CONSOLE_LIST = "openConsoleList";

	public static void main( String[] args ) throws InterruptedException
	{
		PathAndLoggingConfig.configureSystem( args );
		SerialLibraryConfiguration.configure();
//		Gui.run(args);
//		createAndShowGUI();
		
		OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false);
        controller.registerConnectCallback(new Callback() {
            @Override
            public void run(ResponseBase response) {
                GetVersionResponse version = (GetVersionResponse) response;
                System.out.println("Connected!");
                System.out.println(version.getObsStudioVersion());

                controller.registerSwitchScenesCallback( res -> {
                	SwitchScenesResponse switchScene = (SwitchScenesResponse)res;
                	System.out.println("scene changed to " + switchScene.getSceneName());
                });
                
                controller.changeSceneWithTransition( "Offline", "Fade", res -> {
                	SetCurrentSceneResponse setSceneResponse = (SetCurrentSceneResponse)res;
                	System.out.println("scene changed to " + setSceneResponse.getStatus());
                });
            }
        });
		if (controller.isFailed()) { // Awaits response from OBS
			System.out.println( "could not connect to OBS" );
			System.exit( 1 );
		}
		
		while(true) Thread.currentThread().sleep( 1000 );
	}

	private static void createAndShowGUI()
	{
		// Check the SystemTray support
		if ( !SystemTray.isSupported() )
		{
			System.out.println( "SystemTray is not supported" );
			return;
		}
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon( createImage( "/images/bulb.gif", "tray icon" ) );
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem aboutItem = new MenuItem( "About" );
		CheckboxMenuItem cb1 = new CheckboxMenuItem( "Set auto size" );
		CheckboxMenuItem cb2 = new CheckboxMenuItem( "Set tooltip" );
		Menu displayMenu = new Menu( "Display" );
		MenuItem errorItem = new MenuItem( "Error" );
		MenuItem warningItem = new MenuItem( "Warning" );
		MenuItem infoItem = new MenuItem( "Info" );
		MenuItem noneItem = new MenuItem( "None" );
		MenuItem exitItem = new MenuItem( "Exit" );

		// Add components to popup menu
		popup.add( aboutItem );
		popup.addSeparator();
		popup.add( cb1 );
		popup.add( cb2 );
		popup.addSeparator();
		popup.add( displayMenu );
		displayMenu.add( errorItem );
		displayMenu.add( warningItem );
		displayMenu.add( infoItem );
		displayMenu.add( noneItem );
		popup.add( exitItem );

		trayIcon.setPopupMenu( popup );

		try
		{
			tray.add( trayIcon );
		} catch ( AWTException e )
		{
			System.out.println( "TrayIcon could not be added." );
			return;
		}

		trayIcon.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog( null, "This dialog box is run from System Tray" );
			}
		} );

		aboutItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				JOptionPane.showMessageDialog( null, "This dialog box is run from the About menu item" );
			}
		} );

		cb1.addItemListener( new ItemListener()
		{
			public void itemStateChanged( ItemEvent e )
			{
				int cb1Id = e.getStateChange();
				if ( cb1Id == ItemEvent.SELECTED )
				{
					trayIcon.setImageAutoSize( true );
				} else
				{
					trayIcon.setImageAutoSize( false );
				}
			}
		} );

		cb2.addItemListener( new ItemListener()
		{
			public void itemStateChanged( ItemEvent e )
			{
				int cb2Id = e.getStateChange();
				if ( cb2Id == ItemEvent.SELECTED )
				{
					trayIcon.setToolTip( "Sun TrayIcon" );
				} else
				{
					trayIcon.setToolTip( null );
				}
			}
		} );

		ActionListener listener = new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				MenuItem item = (MenuItem)e.getSource();
				// TrayIcon.MessageType type = null;
				System.out.println( item.getLabel() );
				if ( "Error".equals( item.getLabel() ) )
				{
					// type = TrayIcon.MessageType.ERROR;
					trayIcon.displayMessage( "Sun TrayIcon Demo", "This is an error message",
							TrayIcon.MessageType.ERROR );

				} else if ( "Warning".equals( item.getLabel() ) )
				{
					// type = TrayIcon.MessageType.WARNING;
					trayIcon.displayMessage( "Sun TrayIcon Demo", "This is a warning message",
							TrayIcon.MessageType.WARNING );

				} else if ( "Info".equals( item.getLabel() ) )
				{
					// type = TrayIcon.MessageType.INFO;
					trayIcon.displayMessage( "Sun TrayIcon Demo", "This is an info message",
							TrayIcon.MessageType.INFO );

				} else if ( "None".equals( item.getLabel() ) )
				{
					// type = TrayIcon.MessageType.NONE;
					trayIcon.displayMessage( "Sun TrayIcon Demo", "This is an ordinary message",
							TrayIcon.MessageType.NONE );
				}
			}
		};

		errorItem.addActionListener( listener );
		warningItem.addActionListener( listener );
		infoItem.addActionListener( listener );
		noneItem.addActionListener( listener );

		exitItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				tray.remove( trayIcon );
				System.exit( 0 );
			}
		} );
	}

	// Obtain the image URL
	protected static Image createImage( String path, String description )
	{
		URL imageURL = App.class.getResource( path );

		if ( imageURL == null )
		{
			System.err.println( "Resource not found: " + path );
			return null;
		} else
		{
			return (new ImageIcon( imageURL, description )).getImage();
		}
	}
}
