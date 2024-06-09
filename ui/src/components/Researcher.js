import './Researcher.css'
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import GraphMetadata from "./GraphMetadata"
import GraphArchive from "./GraphArchive"


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
      <div className='menu'>
        <div ><button className='button' onClick={handleCreateVertexButton}> Create vertex</button>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
          <button className='button' onClick={handleCreateEdgeButton}> Create edge</button>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
          <button className='button' onClick={handleRemoveVertexButton}> Remove vertex</button>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
        <button className='button' onClick={handleRemoveEdgeButton}> Remove edge</button>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
            <GraphMetadata vertices={props.vertices}
                           edges={props.edges}
                           isWeighted={props.isWeighted}
                           isDirected={props.isDirected}
                           hasMultipleEdges={props.hasMultipleEdges}
                           hasSelfLoops={props.hasSelfLoops}/>  
        </div>  
        <div style={{paddingTop: '2.7rem'}}>
          <div>
            <label> <input type='checkbox' onChange={handleIsDirectedCheckbox}/>  
                Directed
            </label>
          </div>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
          <div>
            <label> <input type='checkbox' onChange={handleIsWeightedCheckbox}/>  
                Weighted
            </label>
          </div>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
            <button className='button' onClick={goToSignInPage}> SignIn</button>
        </div>
        <div style={{paddingTop: '2.7rem'}}>
            <GraphArchive addVertex={props.addVertex}
                          addEdge={props.addEdge}
                          updateVertexCount={props.updateVertexCount}/>
        </div> 
      </div>   
      <div className='canvas'>
        <canvas 
                id='canvas'
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
  hasSelfLoops: PropTypes.number,
  hasMultipleEdges: PropTypes.number,
  addVertex: PropTypes.func,
  addEdge: PropTypes.func,
  updateVertexCount: PropTypes.func,
  vertices: PropTypes.array,
  edges: PropTypes.array
};

export default Researcher;

