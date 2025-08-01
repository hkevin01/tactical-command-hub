import { motion } from 'framer-motion';
import {
    AlertTriangle,
    Bell,
    Clock,
    LogOut,
    Menu,
    Search,
    User,
    Wifi
} from 'lucide-react';
import { useState } from 'react';
import { useTactical } from '../context/TacticalContext';

function Header({ toggleSidebar }) {
  const { state } = useTactical();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfile, setShowProfile] = useState(false);
  
  const currentTime = new Date().toLocaleTimeString();
  const alertCount = state.alerts.length;
  const highPriorityAlerts = state.intelligence.filter(intel => intel.severity === 'HIGH').length;

  return (
    <header className="bg-military-800 border-b border-military-700 px-6 py-4">
      <div className="flex items-center justify-between">
        {/* Left side */}
        <div className="flex items-center space-x-4">
          <button
            onClick={toggleSidebar}
            className="p-2 rounded-md hover:bg-military-700 transition-colors"
          >
            <Menu className="w-5 h-5 text-gray-400" />
          </button>
          
          <div className="relative hidden md:block">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search operations, units, missions..."
              className="tactical-input pl-10 pr-4 py-2 w-96"
            />
          </div>
        </div>

        {/* Right side */}
        <div className="flex items-center space-x-4">
          {/* Real-time clock */}
          <div className="hidden lg:flex items-center space-x-2 text-gray-300">
            <Clock className="w-4 h-4" />
            <span className="font-mono text-sm">{currentTime}</span>
          </div>

          {/* Connection status */}
          <div className="flex items-center space-x-2">
            <Wifi className="w-4 h-4 text-tactical-green" />
            <span className="hidden md:inline text-xs text-gray-400">Connected</span>
          </div>

          {/* Notifications */}
          <div className="relative">
            <button
              onClick={() => setShowNotifications(!showNotifications)}
              className="relative p-2 rounded-md hover:bg-military-700 transition-colors"
            >
              <Bell className="w-5 h-5 text-gray-400" />
              {(alertCount > 0 || highPriorityAlerts > 0) && (
                <span className="absolute -top-1 -right-1 bg-tactical-red text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  {alertCount + highPriorityAlerts}
                </span>
              )}
            </button>

            {showNotifications && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="absolute right-0 top-12 w-80 tactical-card p-4 z-50"
              >
                <h3 className="font-medium text-gray-100 mb-3">Notifications</h3>
                
                {highPriorityAlerts > 0 && (
                  <div className="mb-3 p-3 bg-red-900/50 border border-red-700 rounded-md">
                    <div className="flex items-center space-x-2">
                      <AlertTriangle className="w-4 h-4 text-tactical-red" />
                      <span className="text-sm text-red-300">
                        {highPriorityAlerts} High Priority Intelligence Alert{highPriorityAlerts > 1 ? 's' : ''}
                      </span>
                    </div>
                  </div>
                )}
                
                <div className="space-y-2">
                  <div className="p-2 hover:bg-military-700 rounded-md cursor-pointer">
                    <p className="text-sm text-gray-300">System status: All operational</p>
                    <p className="text-xs text-gray-500">2 minutes ago</p>
                  </div>
                  <div className="p-2 hover:bg-military-700 rounded-md cursor-pointer">
                    <p className="text-sm text-gray-300">Mission progress update</p>
                    <p className="text-xs text-gray-500">5 minutes ago</p>
                  </div>
                  <div className="p-2 hover:bg-military-700 rounded-md cursor-pointer">
                    <p className="text-sm text-gray-300">New unit deployment</p>
                    <p className="text-xs text-gray-500">10 minutes ago</p>
                  </div>
                </div>
                
                <div className="mt-3 pt-3 border-t border-military-700">
                  <button className="text-xs text-primary-400 hover:text-primary-300">
                    View all notifications
                  </button>
                </div>
              </motion.div>
            )}
          </div>

          {/* Profile dropdown */}
          <div className="relative">
            <button
              onClick={() => setShowProfile(!showProfile)}
              className="flex items-center space-x-2 p-2 rounded-md hover:bg-military-700 transition-colors"
            >
              <div className="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center">
                <User className="w-4 h-4 text-white" />
              </div>
              <span className="hidden md:inline text-sm text-gray-300">Commander</span>
            </button>

            {showProfile && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="absolute right-0 top-12 w-48 tactical-card p-4 z-50"
              >
                <div className="mb-3">
                  <p className="font-medium text-gray-100">Commander Smith</p>
                  <p className="text-xs text-gray-400">Tactical Operations</p>
                </div>
                
                <div className="space-y-2">
                  <button className="w-full text-left p-2 text-sm text-gray-300 hover:bg-military-700 rounded-md transition-colors">
                    Profile Settings
                  </button>
                  <button className="w-full text-left p-2 text-sm text-gray-300 hover:bg-military-700 rounded-md transition-colors">
                    Security Preferences
                  </button>
                  <hr className="border-military-600" />
                  <button className="w-full text-left p-2 text-sm text-red-400 hover:bg-military-700 rounded-md transition-colors flex items-center space-x-2">
                    <LogOut className="w-4 h-4" />
                    <span>Logout</span>
                  </button>
                </div>
              </motion.div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
