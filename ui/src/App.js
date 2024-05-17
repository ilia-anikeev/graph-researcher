import {BrowserRouter as Router, Route, Routes} from 'react-router-dom'
import Researcher from './components/Researcher';
import SignIn from './components/SignIn';
import SignUp from './components/SignUp';

function App() {
  return (
    <div >
       <Router>
        <Routes>
            <Route path="/" element={<Researcher/>} />
            <Route path="/signin" element={<SignIn/>} />
            <Route path="/signup" element={<SignUp/>} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
