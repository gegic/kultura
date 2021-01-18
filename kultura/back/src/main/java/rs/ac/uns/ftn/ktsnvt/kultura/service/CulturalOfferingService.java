package rs.ac.uns.ftn.ktsnvt.kultura.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.ktsnvt.kultura.dto.CulturalOfferingDto;
import rs.ac.uns.ftn.ktsnvt.kultura.exception.ResourceExistsException;
import rs.ac.uns.ftn.ktsnvt.kultura.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.ktsnvt.kultura.mapper.Mapper;
import rs.ac.uns.ftn.ktsnvt.kultura.model.CulturalOffering;
import rs.ac.uns.ftn.ktsnvt.kultura.model.CulturalOfferingMainPhoto;
import rs.ac.uns.ftn.ktsnvt.kultura.model.User;
import rs.ac.uns.ftn.ktsnvt.kultura.repository.CulturalOfferingMainPhotoRepository;
import rs.ac.uns.ftn.ktsnvt.kultura.repository.CulturalOfferingRepository;
import rs.ac.uns.ftn.ktsnvt.kultura.repository.UserRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CulturalOfferingService {

    private final CulturalOfferingRepository culturalOfferingRepository;
    private final CulturalOfferingMainPhotoService mainPhotoService;
    private final CulturalOfferingPhotoService culturalOfferingPhotoService;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final Mapper modelMapper;


    @Autowired
    public CulturalOfferingService(CulturalOfferingRepository culturalOfferingRepository,
                                   Mapper modelMapper,
                                   CulturalOfferingMainPhotoService mainPhotoService,
                                   CulturalOfferingPhotoService culturalOfferingPhotoService,
                                   UserRepository userRepository,
                                   ReviewService reviewService) {
        this.culturalOfferingRepository = culturalOfferingRepository;
        this.modelMapper = modelMapper;
        this.mainPhotoService = mainPhotoService;
        this.culturalOfferingPhotoService = culturalOfferingPhotoService;
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }


    public Page<CulturalOfferingDto> readAll(Pageable p,
                                             String searchQuery,
                                             float ratingMin,
                                             float ratingMax,
                                             boolean noReviews,
                                             long categoryId,
                                             long subcategoryId,
                                             float latitudeStart,
                                             float latitudeEnd,
                                             float longitudeStart,
                                             float longitudeEnd,
                                             long userId) {
        Page<CulturalOffering> found;
        if (subcategoryId != -1) {
            if (noReviews) {
                found = culturalOfferingRepository.searchAllNoReviews(p, searchQuery, ratingMin, ratingMax,
                        categoryId, subcategoryId,
                        latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
            } else {
                found = culturalOfferingRepository.searchAll(p, searchQuery, ratingMin, ratingMax,
                        categoryId, subcategoryId,
                        latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
            }
        } else if (categoryId != -1) {
            if (noReviews) {
                found = culturalOfferingRepository.searchAllNoReviews(p, searchQuery, ratingMin, ratingMax,
                        categoryId,
                        latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
            } else {
                found = culturalOfferingRepository.searchAll(p, searchQuery, ratingMin, ratingMax,
                        categoryId,
                        latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
            }
        } else {
            if (noReviews) {
                found = culturalOfferingRepository.searchAllNoReviews(p, searchQuery, ratingMin, ratingMax,
                        latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
            } else {
                found = culturalOfferingRepository.searchAll(p, searchQuery, ratingMin, ratingMax,
                        latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
            }
        }
        Page<CulturalOfferingDto> foundDtos;
        if (userId == -1) {
            foundDtos = found.map(co -> modelMapper.fromEntity(co, CulturalOfferingDto.class));
        } else {
            foundDtos = found.map(co -> {
                CulturalOfferingDto dto = modelMapper.fromEntity(co, CulturalOfferingDto.class);
                dto.setSubscribed(co.getSubscribedUsers().stream().anyMatch(u -> u.getId() == userId));
                return dto;
            });
        }
        return foundDtos;
    }

    public Optional<CulturalOfferingDto> readById(long id, long userId) {
        if (userId == -1) {
            return culturalOfferingRepository.findById(id)
                    .map(co -> modelMapper.fromEntity(co, CulturalOfferingDto.class));
        } else {
            return culturalOfferingRepository.findById(id).map(co -> {
                CulturalOfferingDto dto = modelMapper.fromEntity(co, CulturalOfferingDto.class);
                dto.setSubscribed(co.getSubscribedUsers().stream().anyMatch(u -> u.getId() == userId));
                return dto;
            });
        }
    }

    @Transactional
    public CulturalOfferingDto create(CulturalOfferingDto c) {
        CulturalOffering culturalOffering = modelMapper.fromDto(c, CulturalOffering.class);

        if (c.getId() != null &&
                culturalOfferingRepository.existsById(c.getId())) throw new ResourceExistsException("The cultural offering you are trying to create already exists!");

//        CulturalOfferingMainPhoto photo = photoRepository.getOne(c.getPhotoId());
//        culturalOffering.setPhoto(photo);
        culturalOffering = culturalOfferingRepository.save(culturalOffering);

        return modelMapper.fromEntity(culturalOffering, CulturalOfferingDto.class);
    }

    @Transactional
    public CulturalOfferingDto update(CulturalOfferingDto c) {
        if (c.getId() == null) throw new NullPointerException();

        CulturalOffering toUpdate = culturalOfferingRepository.findById(c.getId())
                .orElseThrow(EntityNotFoundException::new);

        CulturalOffering updateWith = modelMapper.toExistingEntity(c, toUpdate);

        toUpdate.setName(updateWith.getName());
        toUpdate.setAddress(updateWith.getAddress());
        toUpdate.setLatitude(updateWith.getLatitude());
        toUpdate.setLongitude(updateWith.getLongitude());
        toUpdate.setBriefInfo(updateWith.getBriefInfo());
        toUpdate.setAdditionalInfo(updateWith.getAdditionalInfo());
        toUpdate.setSubcategory(toUpdate.getSubcategory());
        CulturalOfferingMainPhoto p = updateWith.getPhoto();
        CulturalOfferingMainPhoto photo = toUpdate.getPhoto();
        if (p.getId() != photo.getId()) {
            mainPhotoService.deletePhoto(photo);
            p.setCulturalOffering(toUpdate);
        }
        toUpdate = culturalOfferingRepository.save(toUpdate);

        return modelMapper.fromEntity(toUpdate, CulturalOfferingDto.class);
    }


    @Transactional
    public List<CulturalOfferingDto> findByBounds(float latitudeStart,
                                                  float latitudeEnd,
                                                  float longitudeStart,
                                                  float longitudeEnd,
                                                  long userId) {
        List<CulturalOffering> found =
                this.culturalOfferingRepository.findByBounds(latitudeStart, latitudeEnd, longitudeStart, longitudeEnd);
        List<CulturalOfferingDto> foundDtos;
        if (userId == -1) {
            foundDtos = found.stream().map(co -> modelMapper.fromEntity(co, CulturalOfferingDto.class))
                    .collect(Collectors.toList());
        } else {
            foundDtos = found.stream().map(co -> {
                CulturalOfferingDto dto = modelMapper.fromEntity(co, CulturalOfferingDto.class);
                dto.setSubscribed(co.getSubscribedUsers().stream().anyMatch(u -> u.getId() == userId));
                return dto;
            }).collect(Collectors.toList());
        }
        return foundDtos;
    }

    @Transactional
    public CulturalOfferingDto subscribe(long culturalOfferingId,
                          long userId) {
        CulturalOffering culturalOffering = this.culturalOfferingRepository.findById(culturalOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Cultural offering with given id was not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with given id was not found."));

        culturalOffering.getSubscribedUsers().add(user);

        culturalOfferingRepository.save(culturalOffering);

        CulturalOfferingDto dto = modelMapper.fromEntity(culturalOffering, CulturalOfferingDto.class);
        dto.setSubscribed(true);
        return dto;
    }

    @Transactional
    public CulturalOfferingDto unsubscribe(long culturalOfferingId,
                            long userId) {
        CulturalOffering culturalOffering = this.culturalOfferingRepository.findById(culturalOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Cultural offering with given id was not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with given id was not found."));

        culturalOffering.getSubscribedUsers().remove(user);

        culturalOfferingRepository.save(culturalOffering);

        return modelMapper.fromEntity(culturalOffering, CulturalOfferingDto.class);
    }

    public void delete(long id) {
        CulturalOffering co = culturalOfferingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cultural offering with given id not found."));

        mainPhotoService.deletePhoto(co.getPhoto());
        culturalOfferingPhotoService.deleteByCulturalOffering(co.getId());
        reviewService.deleteByCulturalOfferingId(co.getId());

        culturalOfferingRepository.deleteById(id);
    }
}
