import './Researcher.css'
import GraphMetadata from "./GraphMetadata"
import { useNavigate } from "react-router-dom";
import PropTypes from 'prop-types';

function Researcher(props) {
  const navigate = useNavigate();

  const goToSignInPage = () => {
    navigate('/signIn');
  }

  const handleCreateVertexButton = () => {
    props.createVertex(200, 200);
  }

  const handleCreateEdgeButton = () => {
    props.setEdgeCreateMode(true);
  }

  const handleRemoveVertexButton = () => {
    props.setVertexRemoveMode(true);
  }

  const handleRemoveEdgeButton = () => {
    props.setEdgeRemoveMode(true);
  }

  const handleIsDirectedCheckbox = () => {
    props.setIsDirected(!props.isDirected);
  }

  const handleIsWeightedCheckbox = () => {
    props.setIsWeighted(!props.isWeighted);
  }

  return (
    <div >
      <div>
        <p className='title'>Graph Researcher</p>
        </div>
      <div className="menu">
        <div ><button className='button' onClick={handleCreateVertexButton}> Create vertex</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={handleCreateEdgeButton}> Create edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={handleRemoveVertexButton}> Remove vertex</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
        <button className='button' onClick={handleRemoveEdgeButton}> Remove edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
            <GraphMetadata vertices={props.vertices}
                           edges={props.edges}/>  
        </div>  
        <div style={{paddingTop: "3rem"}}>
          <div>
            <label> <input type="checkbox" onChange={handleIsDirectedCheckbox}/>  
                Directed
            </label>
          </div>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <div>
            <label> <input type="checkbox" onChange={handleIsWeightedCheckbox}/>  
                Weighted
            </label>
          </div>
        </div>
        <div style={{paddingTop: "3rem"}}>
            <button className='button' onClick={goToSignInPage}> SignIn</button>
        </div>
      </div>   
      <div className="canvas">
        <canvas 
                id="canvas"
                width={window.innerWidth-2}
                height={window.innerHeight-112}>
                Canvas
        </canvas>
      </div>
    </div>
  );
}

Researcher.propTypes = {
  createVertex: PropTypes.func,
  setVertexRemoveMode: PropTypes.func,
  setEdgeRemoveMode: PropTypes.func,
  setEdgeCreateMode: PropTypes.func,
  setIsDirected: PropTypes.func,
  setIsWeighted: PropTypes.func,
  goToSignInPage: PropTypes.func,
  isWeighted: PropTypes.bool,
  isDirected: PropTypes.bool,
  vertices: PropTypes.array,
  edges: PropTypes.array
};

export default Researcher;

