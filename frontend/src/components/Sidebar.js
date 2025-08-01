import { motion } from 'framer-motion';
import {
    Activity,
    ChevronLeft,
    Eye,
    Home,
    Settings,
    Shield,
    Target,
    Users
} from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const menuItems = [
  { path: '/dashboard', icon: Home, label: 'Dashboard' },
  { path: '/units', icon: Users, label: 'Units' },
  { path: '/missions', icon: Target, label: 'Missions' },
  { path: '/intelligence', icon: Eye, label: 'Intelligence' },
  { path: '/settings', icon: Settings, label: 'Settings' },
];

function Sidebar({ isOpen, setIsOpen }) {
  const location = useLocation();

  return (
    <motion.div
      initial={{ x: -280 }}
      animate={{ x: 0 }}
      className={`fixed left-0 top-0 h-full bg-military-800 border-r border-military-700 transition-all duration-300 z-50 ${
        isOpen ? 'w-64' : 'w-16'
      }`}
    >
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b border-military-700">
        <motion.div
          className="flex items-center space-x-3"
          animate={{ opacity: isOpen ? 1 : 0 }}
          transition={{ duration: 0.2 }}
        >
          <Shield className="w-8 h-8 text-primary-500" />
          {isOpen && (
            <div>
              <h1 className="text-lg font-bold text-gray-100">Tactical Hub</h1>
              <p className="text-xs text-gray-400">Command Center</p>
            </div>
          )}
        </motion.div>
        
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="p-1 rounded-md hover:bg-military-700 transition-colors"
        >
          <ChevronLeft 
            className={`w-5 h-5 text-gray-400 transition-transform ${
              !isOpen ? 'rotate-180' : ''
            }`} 
          />
        </button>
      </div>

      {/* Navigation */}
      <nav className="mt-6">
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = location.pathname === item.path;
          
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center px-4 py-3 mx-2 rounded-lg transition-all duration-200 group ${
                isActive
                  ? 'bg-primary-600 text-white'
                  : 'text-gray-300 hover:bg-military-700 hover:text-white'
              }`}
            >
              <Icon className={`w-5 h-5 ${isActive ? 'text-white' : 'text-gray-400'}`} />
              
              {isOpen && (
                <motion.span
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="ml-3 font-medium"
                >
                  {item.label}
                </motion.span>
              )}
              
              {!isOpen && (
                <div className="absolute left-16 bg-military-700 text-white px-2 py-1 rounded-md text-sm invisible group-hover:visible whitespace-nowrap z-50">
                  {item.label}
                </div>
              )}
            </Link>
          );
        })}
      </nav>

      {/* Status Indicator */}
      <div className="absolute bottom-4 left-4 right-4">
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="tactical-card p-3"
          >
            <div className="flex items-center space-x-2">
              <Activity className="w-4 h-4 text-tactical-green" />
              <div className="flex-1">
                <p className="text-xs text-gray-400">System Status</p>
                <p className="text-sm font-medium text-tactical-green">Operational</p>
              </div>
              <div className="w-2 h-2 bg-tactical-green rounded-full glow-effect"></div>
            </div>
          </motion.div>
        )}
      </div>
    </motion.div>
  );
}

export default Sidebar;
