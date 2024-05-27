import './Researcher.css'
import GraphMetadata from "./GraphMetadata"
import { useNavigate } from "react-router-dom";
import PropTypes from 'prop-types';

function Researcher(props) {
  const navigate = useNavigate();

  const goToSignInPage = () => {
    navigate('/signIn');
  }

  const handle = () => {
    props.setIsDirected(!props.isDirected);
  }

  const handle2 = () => {
    props.setIsWeighted(!props.isWeighted);
  }

  return (
    <div >
      <div>
        <p className='title'>Graph Researcher</p>
        </div>
      <div className="menu">
        <div ><button className='button' onClick={() => props.createVertex(200, 200)}> Create vertex</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={() => props.setEdgeCreate(true)}> Create edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={() => props.setVertexRemove(true)}> Remove vertex</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
        <button className='button' onClick={() => props.setEdgeRemove(true)}> Remove edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
            <GraphMetadata setEdgeCreate={props.setEdgeCreate}
                           vertices={props.vertices}
                           edges={props.edges}/>  
        </div>  
        <div style={{paddingTop: "3rem"}}>
          <div>
            <label> <input type="checkbox" onChange={handle}/>  
                Directed
            </label>
          </div>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <div>
            <label> <input type="checkbox" onChange={handle2}/>  
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
  setVertexRemove: PropTypes.func,
  setEdgeRemove: PropTypes.func,
  setEdgeCreate: PropTypes.func,
  setIsDirected: PropTypes.func,
  setIsWeighted: PropTypes.func,
  goToSignInPage: PropTypes.func,
  isWeighted: PropTypes.bool,
  isDirected: PropTypes.bool,
  vertices: PropTypes.array,
  setVertex: PropTypes.func,
  edges: PropTypes.array
};

export default Researcher;

