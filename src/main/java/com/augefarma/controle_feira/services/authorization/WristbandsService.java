package com.augefarma.controle_feira.services.authorization;

import com.augefarma.controle_feira.dtos.authorization.WristbandsResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class WristbandsService {

    private final ParticipantRepository participantRepository;

    @Autowired
    public WristbandsService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }


    public WristbandsResponseDto checkDeliveryOfWristband(String cpfEntity) {
        ParticipantEntity participant = getParticipantByCpf(cpfEntity);

        return returnResponseBasedOnFairCheckIn(getTheLatestFairRegistration(participant), participant);
    }

    @Transactional(readOnly = true)
    private ParticipantEntity getParticipantByCpf(String cpf) {
        return participantRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum participante encontrado"));
    }


    @Transactional(readOnly = true)
    private List<EntryRecordEntity> getTheLatestFairRegistration(ParticipantEntity participant) {

        return participant.getEntryRecords()
                .stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR
                        && entry.getCheckinTime().toLocalDate().isEqual(LocalDate.now())).toList();
    }


    private WristbandsResponseDto returnResponseBasedOnFairCheckIn(
            List<EntryRecordEntity> fairEntryRecordsOnTheCurrentDate, ParticipantEntity participant) {

        if (fairEntryRecordsOnTheCurrentDate.isEmpty()) {
            return new WristbandsResponseDto(participant);
        }

        List<String> formattedCheckInTimes = formatCheckInTimes(fairEntryRecordsOnTheCurrentDate);

        return new WristbandsResponseDto(participant, formattedCheckInTimes);
    }


    private List<String> formatCheckInTimes(List<EntryRecordEntity> entryRecords) {
        return entryRecords.stream()
                .map(record -> record.getCheckinTime().format(formatDateAndTime()))
                .toList();
    }


    private DateTimeFormatter formatDateAndTime() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("pt-BR"));
    }
}
