import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import Graph from './components/Graph';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';
import { UserProvider } from './components/UserContex';

function App() {
  return (
    <div>
      <UserProvider>
        <Router>
          <Routes>
              <Route path="/" element={<Graph/>} />
              <Route path="/signin" element={<SignIn/>} />
              <Route path="/signup" element={<SignUp/>} />
          </Routes>
        </Router>
      </UserProvider>
    </div>
  );
}

export default App;
