package com.shaunmccready.upgradecampsite.service;

import com.shaunmccready.upgradecampsite.domain.Camper;
import com.shaunmccready.upgradecampsite.repository.CamperDao;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CamperService {

    private CamperDao camperDao;

    public CamperService(CamperDao camperDao) {
        this.camperDao = camperDao;
    }

    @Transactional
    public Camper createOrRetrieveCamper(@Valid Camper camper) {
        Optional<Camper> camperByEmail = camperDao.findByEmail(camper.getEmail());

        return camperByEmail.orElseGet(() -> {
            String idOfNewCamper = UUID.randomUUID().toString();
            camper.setId(idOfNewCamper);
            camper.setCreated(LocalDateTime.now());
            camperDao.save(camper);
            return camper;
        });
    }
}
