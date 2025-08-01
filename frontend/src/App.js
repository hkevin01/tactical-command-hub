import { useState } from 'react';
import { Toaster } from 'react-hot-toast';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import { TacticalProvider } from './context/TacticalContext';
import Dashboard from './pages/Dashboard';
import Intelligence from './pages/Intelligence';
import Missions from './pages/Missions';
import Settings from './pages/Settings';
import Units from './pages/Units';

function App() {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <TacticalProvider>
      <Router>
        <div className="flex h-screen bg-military-900">
          <Toaster
            position="top-right"
            toastOptions={{
              style: {
                background: '#1e293b',
                color: '#f1f5f9',
                border: '1px solid #475569',
              },
            }}
          />
          
          {/* Sidebar */}
          <Sidebar isOpen={sidebarOpen} setIsOpen={setSidebarOpen} />
          
          {/* Main content */}
          <div className={`flex-1 flex flex-col transition-all duration-300 ${
            sidebarOpen ? 'ml-64' : 'ml-16'
          }`}>
            <Header toggleSidebar={() => setSidebarOpen(!sidebarOpen)} />
            
            <main className="flex-1 overflow-y-auto p-6">
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/units" element={<Units />} />
                <Route path="/missions" element={<Missions />} />
                <Route path="/intelligence" element={<Intelligence />} />
                <Route path="/settings" element={<Settings />} />
              </Routes>
            </main>
          </div>
        </div>
      </Router>
    </TacticalProvider>
  );
}

export default App;
