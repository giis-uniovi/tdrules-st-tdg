package br.com.codenation.hospital.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.Patient;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.repository.HospitalRepository;
import br.com.codenation.hospital.repository.LocationRepository;
import br.com.codenation.hospital.repository.PatientRepository;
import br.com.codenation.hospital.repository.ProductRepository;

//Added a service for test: clean the database (deleteAll) and get all data (getAll)

@Service
public class TestService {
	
	public class AllData {	
    	public List<Hospital> hospital=new ArrayList<>();
    	public List<Patient> patient=new ArrayList<>();
    	public List<Product> product=new ArrayList<>();
    	public List<Location> location=new ArrayList<>();
    }
	
	@Autowired
	private  HospitalRepository hospitalRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	public void deleteAll() {
		hospitalRepository.deleteAll();
		patientRepository.deleteAll();
		productRepository.deleteAll();
		locationRepository.deleteAll();
	}
	public Object getAll() {
		AllData data = new AllData();
		data.hospital= hospitalRepository.findAll();
		data.patient = patientRepository.findAll();
		data.product = productRepository.findAll();
		data.location= locationRepository.findAll();
		return data;
	}
}
