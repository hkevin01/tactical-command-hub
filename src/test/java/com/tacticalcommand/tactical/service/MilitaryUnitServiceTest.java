package com.tacticalcommand.tactical.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;

/**
 * Unit tests for MilitaryUnitService.
 * 
 * This test class validates the business logic and service layer operations
 * for military unit management. It uses Mockito for dependency mocking
 * and follows AAA (Arrange-Act-Assert) testing pattern.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Military Unit Service Tests")
class MilitaryUnitServiceTest {

    @Mock
    private MilitaryUnitRepository militaryUnitRepository;

    @InjectMocks
    private MilitaryUnitService militaryUnitService;

    private MilitaryUnit testUnit;
    private List<MilitaryUnit> testUnits;

    @BeforeEach
    void setUp() {
        // Arrange: Create test data
        testUnit = new MilitaryUnit();
        testUnit.setId(1L);
        testUnit.setCallsign("ALPHA-1");
        testUnit.setUnitName("1st Infantry Division");
        testUnit.setUnitType(MilitaryUnit.UnitType.INFANTRY);
        testUnit.setDomain(MilitaryUnit.OperationalDomain.LAND);
        testUnit.setStatus(MilitaryUnit.UnitStatus.ACTIVE);
        testUnit.setReadinessLevel(MilitaryUnit.ReadinessLevel.C1);
        testUnit.setLatitude(BigDecimal.valueOf(39.048667));
        testUnit.setLongitude(BigDecimal.valueOf(-76.886944));
        testUnit.setPersonnelCount(250);
        testUnit.setLastContact(LocalDateTime.now().minusMinutes(10));
        testUnit.setCommunicationStatus(MilitaryUnit.CommunicationStatus.OPERATIONAL);
        testUnit.setThreatLevel(MilitaryUnit.ThreatLevel.LOW);

        MilitaryUnit secondUnit = new MilitaryUnit();
        secondUnit.setId(2L);
        secondUnit.setCallsign("BRAVO-2");
        secondUnit.setUnitName("2nd Armored Brigade");
        secondUnit.setUnitType(MilitaryUnit.UnitType.ARMOR);
        secondUnit.setDomain(MilitaryUnit.OperationalDomain.LAND);
        secondUnit.setStatus(MilitaryUnit.UnitStatus.ACTIVE);

        testUnits = Arrays.asList(testUnit, secondUnit);
    }

    @Test
    @DisplayName("Should create military unit successfully")
    void testCreateMilitaryUnit() {
        // Arrange
        when(militaryUnitRepository.save(any(MilitaryUnit.class))).thenReturn(testUnit);

        // Act
        MilitaryUnit result = militaryUnitService.createUnit(testUnit);

        // Assert
        assertNotNull(result);
        assertEquals("ALPHA-1", result.getCallsign());
        assertEquals("1st Infantry Division", result.getUnitName());
        assertEquals(MilitaryUnit.UnitType.INFANTRY, result.getUnitType());
        verify(militaryUnitRepository, times(1)).save(testUnit);
    }

    @Test
    @DisplayName("Should find military unit by ID")
    void testFindUnitById() {
        // Arrange
        when(militaryUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));

        // Act
        Optional<MilitaryUnit> result = militaryUnitService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ALPHA-1", result.get().getCallsign());
        verify(militaryUnitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when unit not found by ID")
    void testFindUnitByIdNotFound() {
        // Arrange
        when(militaryUnitRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<MilitaryUnit> result = militaryUnitService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(militaryUnitRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find military unit by callsign")
    void testFindUnitByCallsign() {
        // Arrange
        when(militaryUnitRepository.findByCallsign("ALPHA-1")).thenReturn(Optional.of(testUnit));

        // Act
        Optional<MilitaryUnit> result = militaryUnitService.findByCallsign("ALPHA-1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ALPHA-1", result.get().getCallsign());
        verify(militaryUnitRepository, times(1)).findByCallsign("ALPHA-1");
    }

    @Test
    @DisplayName("Should get all military units with pagination")
    void testGetAllUnits() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<MilitaryUnit> expectedPage = new PageImpl<>(testUnits, pageable, testUnits.size());
        when(militaryUnitRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<MilitaryUnit> result = militaryUnitService.getAllUnits(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("ALPHA-1", result.getContent().get(0).getCallsign());
        verify(militaryUnitRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find units by status")
    void testFindUnitsByStatus() {
        // Arrange
        when(militaryUnitRepository.findByStatus(MilitaryUnit.UnitStatus.ACTIVE))
                .thenReturn(testUnits);

        // Act
        List<MilitaryUnit> result = militaryUnitService.findByStatus(MilitaryUnit.UnitStatus.ACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(unit -> unit.getStatus() == MilitaryUnit.UnitStatus.ACTIVE));
        verify(militaryUnitRepository, times(1)).findByStatus(MilitaryUnit.UnitStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find units within radius")
    void testFindUnitsWithinRadius() {
        // Arrange
        double latitude = 39.048667;
        double longitude = -76.886944;
        double radiusKm = 10.0;
        when(militaryUnitRepository.findUnitsWithinRadius(
                BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude), radiusKm))
                .thenReturn(Arrays.asList(testUnit));

        // Act
        List<MilitaryUnit> result = militaryUnitService.findUnitsWithinRadius(latitude, longitude, radiusKm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ALPHA-1", result.get(0).getCallsign());
        verify(militaryUnitRepository, times(1))
                .findUnitsWithinRadius(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude), radiusKm);
    }

    @Test
    @DisplayName("Should update unit position")
    void testUpdateUnitPosition() {
        // Arrange
        BigDecimal newLatitude = BigDecimal.valueOf(39.050000);
        BigDecimal newLongitude = BigDecimal.valueOf(-76.890000);
        BigDecimal newAltitude = BigDecimal.valueOf(100.0);
        when(militaryUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(militaryUnitRepository.save(any(MilitaryUnit.class))).thenReturn(testUnit);

        // Act
        MilitaryUnit result = militaryUnitService.updateUnitPosition(1L, newLatitude, newLongitude, newAltitude);

        // Assert
        assertNotNull(result);
        assertEquals(newLatitude, result.getLatitude());
        assertEquals(newLongitude, result.getLongitude());
        verify(militaryUnitRepository, times(1)).findById(1L);
        verify(militaryUnitRepository, times(1)).save(testUnit);
    }

    @Test
    @DisplayName("Should throw exception when updating position of non-existent unit")
    void testUpdateUnitPositionNotFound() {
        // Arrange
        when(militaryUnitRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                militaryUnitService.updateUnitPosition(999L, 
                    BigDecimal.valueOf(39.0), BigDecimal.valueOf(-76.0), BigDecimal.valueOf(100.0)));
        verify(militaryUnitRepository, times(1)).findById(999L);
        verify(militaryUnitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update unit status")
    void testUpdateUnitStatus() {
        // Arrange
        MilitaryUnit.UnitStatus newStatus = MilitaryUnit.UnitStatus.MAINTENANCE;
        when(militaryUnitRepository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(militaryUnitRepository.save(any(MilitaryUnit.class))).thenReturn(testUnit);

        // Act
        MilitaryUnit result = militaryUnitService.updateUnitStatus(1L, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(militaryUnitRepository, times(1)).findById(1L);
        verify(militaryUnitRepository, times(1)).save(testUnit);
    }

    @Test
    @DisplayName("Should delete military unit")
    void testDeleteUnit() {
        // Arrange
        when(militaryUnitRepository.existsById(1L)).thenReturn(true);
        doNothing().when(militaryUnitRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> militaryUnitService.deleteUnit(1L));
        verify(militaryUnitRepository, times(1)).existsById(1L);
        verify(militaryUnitRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent unit")
    void testDeleteUnitNotFound() {
        // Arrange
        when(militaryUnitRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> militaryUnitService.deleteUnit(999L));
        verify(militaryUnitRepository, times(1)).existsById(999L);
        verify(militaryUnitRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should find units by domain")
    void testFindUnitsByDomain() {
        // Arrange
        when(militaryUnitRepository.findByDomain(MilitaryUnit.OperationalDomain.LAND))
                .thenReturn(testUnits);

        // Act
        List<MilitaryUnit> result = militaryUnitService.findByDomain(MilitaryUnit.OperationalDomain.LAND);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(unit -> unit.getDomain() == MilitaryUnit.OperationalDomain.LAND));
        verify(militaryUnitRepository, times(1)).findByDomain(MilitaryUnit.OperationalDomain.LAND);
    }

    @Test
    @DisplayName("Should find units by readiness level")
    void testFindUnitsByReadinessLevel() {
        // Arrange
        when(militaryUnitRepository.findByReadinessLevel(MilitaryUnit.ReadinessLevel.C1))
                .thenReturn(Arrays.asList(testUnit));

        // Act
        List<MilitaryUnit> result = militaryUnitService.findByReadinessLevel(MilitaryUnit.ReadinessLevel.C1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MilitaryUnit.ReadinessLevel.C1, result.get(0).getReadinessLevel());
        verify(militaryUnitRepository, times(1))
                .findByReadinessLevel(MilitaryUnit.ReadinessLevel.C1);
    }

    @Test
    @DisplayName("Should validate unit data before creation")
    void testValidateUnitData() {
        // Arrange
        MilitaryUnit invalidUnit = new MilitaryUnit();
        // Missing required fields

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
                militaryUnitService.createUnit(invalidUnit));
        verify(militaryUnitRepository, never()).save(any());
    }
}
