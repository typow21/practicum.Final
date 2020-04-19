package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

//Code in here is only for creating a contact, deleting a contact, and fetching the contacts
//NO CODE FOR THE REST IN HERE, LEAVING ALL OF THAT IN THE REST CONTROLLER, ITS CLEANER AND EASIER TO READ

@Component 
public class ContactsStore {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	//**Where do I store the file?**
	public static final File storeFile = new File("./store.json");
	
	//A list of objects where each object, Contact, will hold the parameters passed in
	private List <Contact> contactList = new ArrayList<>();
	//Possibly put id for contact here 
	
	private int id = 0;
	
	//Returns the contacts stored in the contactList
	public List <Contact> fetchContacts() {
		
		return this.contactList;
		
	}
	
	//Method creates a single contact
	//This method will *get* and save the data into a contact data structure, from the page where the contact is created.
	//Then the contact is added to an eventsLists which is basically a list of contacts, and returns the contact created.
	public Contact createContacts(String firstName, String lastName, String phoneNumber, String address, String birthday, List<Integer> relationships) {
		
		//Actual item, that is of type Contact, to add to the list of contacts
		Contact item = new Contact(id, firstName, lastName, phoneNumber, address, birthday, relationships);
		
		//Add the event to the event list
		contactList.add(item);
		
		//increment id here?
		this.id++;
		
		this.saveContacts();
		
		return item;
		
	}
	
	//Deletes a contact according to the id given
	public void deleteContact(int contactId) {
		
		Integer indexToDelete = null;
		
		//Go through list to find contact to remove 
		for (int i = 0; i < contactList.size(); i++) {
			
			//Check if found the contact to remove from the list
			if(contactList.get(i).id == contactId) {
				
				indexToDelete = i;
				//Break out of for loop
				break;
				
			}
			
		}
		
		//If found the index of the contact to delete
		if (indexToDelete != null) {
			
			//Remove that contact from the contactList, do so by casting the object indexToDelete, of type Integer, into an int 
			contactList.remove((int)indexToDelete);
			for(int i = indexToDelete; i<contactList.size(); i++) {
				contactList.get(i).id--;
				for(Contact contact : contactList) {
					for(Integer num : contact.relationships) {
						if(num > indexToDelete) {
							num--;
						}
					}
				}
			}
			for(Contact contact : contactList) {
				for(Integer num : contact.relationships) {
					if(num.equals(indexToDelete)) {
						num = -1;
					}
				}
			}
			id = contactList.size();
		//If contact to be deleted doesn't exist or wasn't found, print out a message	
		} else {
			
			throw new IllegalArgumentException ("Contact either doesn't exist or was not found");
			
			//System.out.println("Contact either didn't exist or was not found");
			
		}
		
		//Save the changes to the json file, which will be that the contact was deleted or not deleted at all
		this.saveContacts();
		
	}
	
	//Method updates a contact that is being changed by the user
	public void editContact(int contactId, String firstName, String lastName, String phoneNumber, String address, String birthday) {
		
		Integer indexToEdit = null;
		
		//Go through list to find contact to edit 
		for (int i = 0; i < contactList.size(); i++) {
					
			//Check if found the contact to edit from the list
			if(contactList.get(i).id == contactId) {
						
				indexToEdit = i;
				//Break out of for loop
				break;
						
			}
					
		}
		
		//If found the index of the contact to edit, edit all the fields. If the field isn't changed then nothing was passed in to change that field
		if (indexToEdit != null) {
			
			contactList.get((int)indexToEdit).firstName = firstName;
			contactList.get((int)indexToEdit).lastName = lastName;
			contactList.get((int)indexToEdit).phoneNumber = phoneNumber;
			contactList.get((int)indexToEdit).address = address;
			contactList.get((int)indexToEdit).birthday = birthday;
		
		//If contact to be edited doesn't exist or wasn't found, print out a message	
		} else {
			
			throw new IllegalArgumentException ("Contact could not be edited");
			//System.out.println("Contact could not be edited");
			
		}
		
		//Save the changes to the contact, or the new contact, into the json file 
		this.saveContacts();
		
	}
	
	@PostConstruct
	private void reloadContacts() throws IOException {
		
		//If file can be read
		if(storeFile.exists()) {
			
			
			this.contactList = mapper.readValue(storeFile, new TypeReference<List<Contact>>(){});
			
			System.out.println("Loaded " + this.contactList.size() + " from the JSON file");
			
		} else {
			
			System.out.println("Creating Contacts list");
			
		}
		
	}
	
	private void saveContacts() {
		
		try {
			
			mapper.writeValue(storeFile, this.contactList);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		
	}

}
