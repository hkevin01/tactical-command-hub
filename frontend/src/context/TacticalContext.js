import { createContext, useContext, useEffect, useReducer } from 'react';
import toast from 'react-hot-toast';

// Sample tactical data
const initialState = {
  units: [
    {
      id: 1,
      callsign: "ALPHA-6",
      unitName: "Alpha Company",
      unitType: "Infantry",
      domain: "LAND",
      status: "OPERATIONAL",
      currentLatitude: 34.0522,
      currentLongitude: -118.2437,
      altitude: 100.0,
      heading: 90.0,
      speed: 5.5,
      maxPersonnel: 120,
      currentPersonnel: 118,
      equipmentStatus: "FULLY_OPERATIONAL",
      lastReportTime: "2025-01-27T14:30:00"
    },
    {
      id: 2,
      callsign: "BRAVO-7",
      unitName: "Bravo Company",
      unitType: "Armor",
      domain: "LAND",
      status: "MAINTENANCE",
      currentLatitude: 34.0622,
      currentLongitude: -118.2537,
      altitude: 105.0,
      heading: 135.0,
      speed: 0,
      maxPersonnel: 80,
      currentPersonnel: 76,
      equipmentStatus: "PARTIAL",
      lastReportTime: "2025-01-27T13:45:00"
    },
    {
      id: 3,
      callsign: "CHARLIE-1",
      unitName: "Charlie Squadron",
      unitType: "Aviation",
      domain: "AIR",
      status: "OPERATIONAL",
      currentLatitude: 34.0722,
      currentLongitude: -118.2337,
      altitude: 1500.0,
      heading: 270.0,
      speed: 150.0,
      maxPersonnel: 6,
      currentPersonnel: 6,
      equipmentStatus: "FULLY_OPERATIONAL",
      lastReportTime: "2025-01-27T14:35:00"
    }
  ],
  missions: [
    {
      id: 1,
      name: "Operation Thunder Strike",
      type: "Reconnaissance",
      status: "ACTIVE",
      priority: "HIGH",
      startDate: "2025-01-27T08:00:00",
      endDate: "2025-01-27T18:00:00",
      assignedUnits: [1, 3],
      progress: 65,
      description: "Conduct aerial reconnaissance of suspected enemy positions"
    },
    {
      id: 2,
      name: "Checkpoint Alpha",
      type: "Security",
      status: "ACTIVE",
      priority: "MEDIUM",
      startDate: "2025-01-27T06:00:00",
      endDate: "2025-01-28T06:00:00",
      assignedUnits: [1],
      progress: 45,
      description: "Maintain security checkpoint at strategic location"
    },
    {
      id: 3,
      name: "Equipment Maintenance",
      type: "Logistics",
      status: "IN_PROGRESS",
      priority: "LOW",
      startDate: "2025-01-27T10:00:00",
      endDate: "2025-01-27T16:00:00",
      assignedUnits: [2],
      progress: 30,
      description: "Scheduled maintenance of armored vehicles"
    }
  ],
  intelligence: [
    {
      id: 1,
      title: "Enemy Movement Detected",
      type: "THREAT",
      severity: "HIGH",
      timestamp: "2025-01-27T14:20:00",
      location: { lat: 34.0823, lng: -118.2523 },
      description: "Multiple hostile vehicles observed moving southeast",
      verified: true
    },
    {
      id: 2,
      title: "Civilian Activity",
      type: "INFO",
      severity: "LOW",
      timestamp: "2025-01-27T13:55:00",
      location: { lat: 34.0423, lng: -118.2323 },
      description: "Increased civilian traffic in area delta",
      verified: false
    },
    {
      id: 3,
      title: "Weather Alert",
      type: "ENVIRONMENTAL",
      severity: "MEDIUM",
      timestamp: "2025-01-27T12:30:00",
      location: { lat: 34.0622, lng: -118.2437 },
      description: "Incoming storm system, visibility will be reduced",
      verified: true
    }
  ],
  alerts: [],
  loading: false,
  error: null
};

const TacticalContext = createContext();

// Action types
const actionTypes = {
  SET_LOADING: 'SET_LOADING',
  SET_ERROR: 'SET_ERROR',
  ADD_UNIT: 'ADD_UNIT',
  UPDATE_UNIT: 'UPDATE_UNIT',
  DELETE_UNIT: 'DELETE_UNIT',
  ADD_MISSION: 'ADD_MISSION',
  UPDATE_MISSION: 'UPDATE_MISSION',
  DELETE_MISSION: 'DELETE_MISSION',
  ADD_INTELLIGENCE: 'ADD_INTELLIGENCE',
  UPDATE_INTELLIGENCE: 'UPDATE_INTELLIGENCE',
  DELETE_INTELLIGENCE: 'DELETE_INTELLIGENCE',
  ADD_ALERT: 'ADD_ALERT',
  REMOVE_ALERT: 'REMOVE_ALERT',
  SIMULATE_REAL_TIME_UPDATE: 'SIMULATE_REAL_TIME_UPDATE'
};

// Reducer function
function tacticalReducer(state, action) {
  switch (action.type) {
    case actionTypes.SET_LOADING:
      return { ...state, loading: action.payload };
    
    case actionTypes.SET_ERROR:
      return { ...state, error: action.payload, loading: false };
    
    case actionTypes.ADD_UNIT:
      return {
        ...state,
        units: [...state.units, { ...action.payload, id: Date.now() }]
      };
    
    case actionTypes.UPDATE_UNIT:
      return {
        ...state,
        units: state.units.map(unit =>
          unit.id === action.payload.id ? { ...unit, ...action.payload } : unit
        )
      };
    
    case actionTypes.DELETE_UNIT:
      return {
        ...state,
        units: state.units.filter(unit => unit.id !== action.payload)
      };
    
    case actionTypes.ADD_MISSION:
      return {
        ...state,
        missions: [...state.missions, { ...action.payload, id: Date.now() }]
      };
    
    case actionTypes.UPDATE_MISSION:
      return {
        ...state,
        missions: state.missions.map(mission =>
          mission.id === action.payload.id ? { ...mission, ...action.payload } : mission
        )
      };
    
    case actionTypes.DELETE_MISSION:
      return {
        ...state,
        missions: state.missions.filter(mission => mission.id !== action.payload)
      };
    
    case actionTypes.ADD_INTELLIGENCE:
      return {
        ...state,
        intelligence: [action.payload, ...state.intelligence]
      };
    
    case actionTypes.UPDATE_INTELLIGENCE:
      return {
        ...state,
        intelligence: state.intelligence.map(intel =>
          intel.id === action.payload.id ? { ...intel, ...action.payload } : intel
        )
      };
    
    case actionTypes.DELETE_INTELLIGENCE:
      return {
        ...state,
        intelligence: state.intelligence.filter(intel => intel.id !== action.payload)
      };
    
    case actionTypes.ADD_ALERT:
      return {
        ...state,
        alerts: [action.payload, ...state.alerts]
      };
    
    case actionTypes.REMOVE_ALERT:
      return {
        ...state,
        alerts: state.alerts.filter(alert => alert.id !== action.payload)
      };
    
    case actionTypes.SIMULATE_REAL_TIME_UPDATE:
      // Simulate real-time updates for demo purposes
      const updatedUnits = state.units.map(unit => ({
        ...unit,
        speed: Math.max(0, unit.speed + (Math.random() - 0.5) * 2),
        heading: (unit.heading + (Math.random() - 0.5) * 10) % 360,
        lastReportTime: new Date().toISOString()
      }));
      
      const updatedMissions = state.missions.map(mission => ({
        ...mission,
        progress: Math.min(100, mission.progress + Math.random() * 2)
      }));
      
      return {
        ...state,
        units: updatedUnits,
        missions: updatedMissions
      };
    
    default:
      return state;
  }
}

export function TacticalProvider({ children }) {
  const [state, dispatch] = useReducer(tacticalReducer, initialState);

  // Simulate real-time updates
  useEffect(() => {
    const interval = setInterval(() => {
      dispatch({ type: actionTypes.SIMULATE_REAL_TIME_UPDATE });
    }, 5000); // Update every 5 seconds

    return () => clearInterval(interval);
  }, []);

  // Action creators
  const actions = {
    setLoading: (loading) => dispatch({ type: actionTypes.SET_LOADING, payload: loading }),
    setError: (error) => dispatch({ type: actionTypes.SET_ERROR, payload: error }),
    
    addUnit: (unit) => {
      dispatch({ type: actionTypes.ADD_UNIT, payload: unit });
      toast.success(`Unit ${unit.callsign} added successfully`);
    },
    
    updateUnit: (unit) => {
      dispatch({ type: actionTypes.UPDATE_UNIT, payload: unit });
      toast.success(`Unit ${unit.callsign} updated`);
    },
    
    deleteUnit: (unitId) => {
      const unit = state.units.find(u => u.id === unitId);
      dispatch({ type: actionTypes.DELETE_UNIT, payload: unitId });
      toast.success(`Unit ${unit?.callsign} removed`);
    },
    
    addMission: (mission) => {
      dispatch({ type: actionTypes.ADD_MISSION, payload: mission });
      toast.success(`Mission "${mission.name}" created`);
    },
    
    updateMission: (mission) => {
      dispatch({ type: actionTypes.UPDATE_MISSION, payload: mission });
      toast.success(`Mission "${mission.name}" updated`);
    },
    
    deleteMission: (missionId) => {
      const mission = state.missions.find(m => m.id === missionId);
      dispatch({ type: actionTypes.DELETE_MISSION, payload: missionId });
      toast.success(`Mission "${mission?.name}" deleted`);
    },
    
    addIntelligence: (intel) => {
      const newIntel = { ...intel, id: Date.now(), timestamp: new Date().toISOString() };
      dispatch({ type: actionTypes.ADD_INTELLIGENCE, payload: newIntel });
      toast.success('Intelligence report added');
    },
    
    updateIntelligence: (intel) => {
      dispatch({ type: actionTypes.UPDATE_INTELLIGENCE, payload: intel });
      toast.success('Intelligence updated');
    },
    
    deleteIntelligence: (intelId) => {
      dispatch({ type: actionTypes.DELETE_INTELLIGENCE, payload: intelId });
      toast.success('Intelligence report deleted');
    },
    
    addAlert: (alert) => {
      const newAlert = { ...alert, id: Date.now(), timestamp: new Date().toISOString() };
      dispatch({ type: actionTypes.ADD_ALERT, payload: newAlert });
      toast.error(alert.message);
    },
    
    removeAlert: (alertId) => {
      dispatch({ type: actionTypes.REMOVE_ALERT, payload: alertId });
    }
  };

  return (
    <TacticalContext.Provider value={{ state, actions }}>
      {children}
    </TacticalContext.Provider>
  );
}

export function useTactical() {
  const context = useContext(TacticalContext);
  if (!context) {
    throw new Error('useTactical must be used within a TacticalProvider');
  }
  return context;
}
