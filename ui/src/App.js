import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Graph from './components/Graph';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import FileUploader from './components/FileUploader'; 
import GraphSearch from './components/GraphSearch';
import { UserProvider } from './components/UserContex';
import {AppProvider} from './AppContext';
function App() {
  return (
    <div>
      <AppProvider>
      <UserProvider>
        <Router>
          <Routes>
              <Route path="/" element={<Graph />} />
              <Route path="/signin" element={<SignIn />} />
              <Route path="/signup" element={<SignUp />} />
              <Route path="/upload" element={<FileUploader />} />
              <Route path="/search" element={<GraphSearch />} />
          </Routes>
        </Router>
      </UserProvider>
      </AppProvider>
    </div>
  );
}

export default App;