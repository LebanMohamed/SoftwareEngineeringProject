   _____ __  __  _____ _____  _____ 
  / ____|  \/  |/ ____|_   _|/ ____|
 | |  __| \  / | (___   | | | (___  
 | | |_ | |\/| |\___ \  | |  \___ \ 
 | |__| | |  | |____) |_| |_ ____) |
  \_____|_|  |_|_____/|_____|_____/ 
	            README

Table of Contents
1.  How To Run The Application
2.  Authentication
2.1 Users
3.  Customers
4.  Vehicles
5.  Parts
6.  Diagnosis & Repair (Bookings)
6.1 Mechanics
7.  Specialist Repairs (SPC)

Contents
1. How To Run The Application
Simply double-click 'gmsis.jar' and everything should work from there onwards.
The database and external libraries are embedded in the application JAR.

Note: If your Java installation is not configured properly, you may have to run it
 using the command line, e.g. java -jar gmsis.jar

2. Authentication
The first thing you will see when you start the application is a Login window.
Type in your credentials and press the 'Login' button to continue to the SystemOverviewFrame,
 or the 'Cancel' button to exit the program.

Typing an incorrect id, incorrect password or both will tell you that you typed an
 incorrect password, this is to hamper brute-force efforts
 (since you cannot tell if the user id is valid or not).

Credentials of all day-to-day user accounts in the system (Format: user:pass)
10000:12345			  Admin account
10001:12345			  Admin account
34343:eqei2oq		  Admin account
44444:4qeqe2er		Admin account
44644:cedwe2e3
45656:6dddwwee2e
54545:55eaewwed2
66666:dqd2imd$£
66676:com$%^PLEX!!
94644:lk33212

Once you login you will have access to the SystemOverviewFrame window.
Here you can interact with each module or logout.
Logging out will take you back to the Login window.

Tge Manage Users/Manage Specialist Repair Centres buttons are only interactable with
 if you are logged in on an admin account.
In Manage Users you can manage the day-to-day user accounts of the system.
In Manage Specialist Repair Centres you can manage the SPCs associated with this garage.

1.1 Users
Here you can add users (click the 'Add' button on the bottom-left),
 remove users (click a user in the table and then click 'Remove' button on the bottom-left),
 edit users (double click a cell in the table and press enter once done - all except user ID are editable, including the system admin checkbox).
Click the 'Done' button in the bottom-right to return to the SystemOverviewFrame when finished.
Note: Users must have unique IDs and names (first name and surname pair) or addition will fail.
Note: You cannot delete your own account from the system.
Note: You can un-admin yourself, but this will lock your account out from the Manage Users window (it will close immediately).

3. Customers
The window that opens allows you to add, edit, remove and search customers. 
The reset search button is used to refresh the table model to get all customers up again once searched.
The done button closes the customer account window, showing the overview window. 

The menu allows you to access a list of future and past bookings, parts used, vehicles and invoice.
You can also search for a booking by customer name and search for vehicle by customer id. You can start a booking by ‘initiating booking’.

4. Vehicles
The Window that opens allows you to add a vehicle and edit/delete a selected vehicle it 
also allows you to display customer details for a selected vehicle. When editing and 
adding a vehicle record the warranty ID dropdown will be disabled as long as the 
hasWarranty checkbox is unticked It also allows you to view and edit the warranty details
of a selected vehicle if it has warranty details. It also allows for a addition of a 
warranty company for quick selection when adding or editing a vehicle record. When you 
select a vehicle it automatically displays all the parts used/installed. 
You can also see the bookings for the selected bookings by selecting the required button. 
To search, you select the criteria from the drop down and type in the search terms and 
this allows for partial entry and ignores case and search which will then 
display the list meeting te criteria nad you can delete and edit from this. You can also 
rest the search afterwards. pressing the doen button or exiting the window will take you 
back to the home page(SystemsOverviewFrame).

5. Parts
The parts button will open a window which will show stock parts. From the stock part menu, you will be able to open install parts, part deliveries and booking parts (Which contain part withdrawal information). You will also be able to add/edit/delete stock parts. Once install parts are opened, you will be able to search by customer first or second name or vehicle registration. You can only use the filter for one of the fields. You are able to add/edit/delete install parts. Once you are finished using install parts click done and this will close the window and only show the original stock parts window. After clicking manage parts booked, the corresponding window will pop up and from here you will be able to see all the parts connected to a booking. To only see part withdrawals, click the part withdrawals button and another window will pop up showing you the part withdrawals and then click done to close the window. From the manage parts booked window, you are able to add/edit/delete parts booked. To add/edit/delete part withdrawals, simply ensure the type is always replacement. There are 2 types which are replacement and repair. Repair is a part sent to the spc to be repaired and replacement is a normal part added to a repair vehicle booking. After you are done with the manage parts booked simply 

click done. To view part deliveries, click part deliveries in the menu of the stock parts window and the part deliveries window will pop up. To add/edit/delete part deliveries click the corresponding button in the menu bar. Once you are finished with the part delivreries window, click done. Once finished with the parts module, click the cross button at the top of the stock parts window.

6. Diagnosis & Repair (Bookings)
This window allows you to add/edit/view/remove bookings from the system.
Note: When adding a booking you will have to type in the time spent by a mechanic manually, this is because a booking
 could technically end earlier than expected.
These interactions are available by using their respective Menu Items (Menu Item: File->...).
Note: Editing/viewing/removing bookings requires you to click a booking in the table first, then click the menu item.
Note: In add/edit booking, if you click 'edit parts' or 'delegate to spc',
the current details in the booking will be saved (if valid) and then the relevant window opened to finish the process.

You can also see charts representing the composition of existing bookings using the menu bar (Menu Item: File->Charts).
Click the 'Done' button in the bottom-right when finished looking at them, and click the tab buttons to look after different charts.

You can also search for bookings by various filters (Menu Item: File->Search...).
You can also revert the bookings to the initial ones you saw (Menu Item: File->Reset Search)
Note: Some filters may require the choose button, to select the value to search for.
 This button will become enabled if this is the case.

Clicking the menu-item: File->Done or exiting the window will go back to the SystemOverviewFrame window.

6.1 Mechanics

The add/edit/remove/view mechanics click Menu Item: File->Mechanics.
You can add mechanics by clicking the 'Add' button in the bottom-left of the window.
Note: Mechanics must have unique IDs and names (first name and surname pair) or addition will fail.
You can remove mechanics by clicking on them, and then the 'Remove' button in the bottom-right.
Note: This operation will fail if they are assigned to any bookings in the system.
 You will have to unassign them from any bookings they are in first, this is impossible
 if they are involved in a booking already because otherwise the invoice details will be incorrect.
This data in this table is editable by double-clicking the cells in the table and pressing enter once done (except the ID column).
Note: The hourly wage column is rounded to 2 decimal places.

7. Specialist Repairs (SPC)
The window allows you to add/delete SPC bookings, view summary of outstanding items, view centres used on a vehicle, view vehicles at SPC. To view vehicles at a Specialist Repairs Centre, you should click on Menu ->  Show list of repair centres. This would open a new window that allows you to choose a centre. After the centre is chosen, click on the button to view vehicles sent to the selected centre.
To add/delete/edit parts, you must click Menu->View centres used on a vehicle. You must then select a vehicle, the list of SPCs used should appear automatically. By selecting an SPC centre from this list and clicking on ‘Show parts’ a new window will appear with the list of parts. If this list is empty, that means the selected SPC has vehicle only. To add/edit/delete parts you must click on a corresponding button. You can not edit parts if the booking end date has passed.You can not add a part if there is currently no ongoing booking. 
Where the search box is available, type in vehicle registration number and press ‘Search’ if you want to see specific information only.
