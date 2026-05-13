package fr.honeygroup.bll.impl;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;
import fr.honeygroup.mapper.PoleMapper;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.bll.PoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PoleServiceImpl implements PoleService {

    // ✅ injection par constructeur (via final + constructor Lombok)
    private final PoleRepository poleRepository;
    private final PoleMapper mapper;
    private final MessageSource messageSource;

    public PoleServiceImpl(PoleRepository poleRepository,
                          PoleMapper mapper,
                          MessageSource messageSource) {
        this.poleRepository = poleRepository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    /* 🌍 i18n helper
    private String msg(String key) {
        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );
    }*/

    // ======================
    // GET ALL
    // ======================
    @Override
    public List<PoleResponse> getAll() {
        return poleRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // ======================
    // GET BY ID
    // ======================
    @Override
    public PoleResponse getById(Long id) {

        Pole pole = poleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pôle introuvable"));

        return mapper.toResponse(pole);
    }

    // ======================
    // GET BY NOM
    // ======================
    @Override
    public PoleResponse getByNom(String nom) {

        Pole pole = poleRepository.findByNom(nom)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pôle introuvable avec le nom : " + nom
                        )
                );

        return mapper.toResponse(pole);
    }

    // ======================
    // DELETE BY ID
    // ======================
     @Override
    public void deleteById(Long id) {

        Pole pole = poleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pôle introuvable"));

        // 🔥 règle métier
       /* if (pole.getPrestations() != null && !pole.getPrestations().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer un pôle avec des prestations"));
        }  */

        poleRepository.deleteById(id);
    }

	@Override
	public PoleResponse create(PoleRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PoleResponse update(Long id, PoleRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}