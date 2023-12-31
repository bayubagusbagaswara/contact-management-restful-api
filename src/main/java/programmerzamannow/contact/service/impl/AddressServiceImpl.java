package programmerzamannow.contact.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import programmerzamannow.contact.dto.AddressResponse;
import programmerzamannow.contact.dto.CreateAddressRequest;
import programmerzamannow.contact.entity.Address;
import programmerzamannow.contact.entity.Contact;
import programmerzamannow.contact.entity.User;
import programmerzamannow.contact.repository.AddressRepository;
import programmerzamannow.contact.repository.ContactRepository;
import programmerzamannow.contact.service.AddressService;
import programmerzamannow.contact.service.ValidationService;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final ContactRepository contactRepository;

    private final ValidationService validationService;

    public AddressServiceImpl(AddressRepository addressRepository, ContactRepository contactRepository, ValidationService validationService) {
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.validationService = validationService;
    }

    @Transactional
    @Override
    public AddressResponse create(User user, CreateAddressRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    @Override
    public AddressResponse get(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
