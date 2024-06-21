import PropTypes from 'prop-types';
import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Checkbox, Input } from 'antd'
import { UserContext } from './UserContex';
import './Researcher.css'
import ResearchInfo from './ResearchInfo';
import GraphArchive from './GraphArchive';
import UserGraphs from './UserGraphs'

function Researcher(props) {
  const {userID} = useContext(UserContext);
  const [graphName, setGraphName] = useState('');
  const [graphResearchInfo, setGraphResearchInfo] = useState(null);
  const [comment, setComment] = useState('');
  const [source, setSource] = useState('');
  const [sink, setSink] = useState('');
  const [isGraphSaveMode, setIsGraphSaveMode] = useState(false);
  const [isAddCommentMode, setAddCommentMode] = useState(false);
  
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


  const saveGraph = () => {
    setIsGraphSaveMode(false);
    setAddCommentMode(false);
    console.log(comment);
    if (props.vertices.length === 0) {
      return;
    }
    const hasMultipleEdges = props.hasMultipleEdges > 0 ? true : false;
    const hasSelfLoops = props.hasSelfLoops > 0 ? true : false;
    const edges = props.edges.map(edge => {
        const source = props.vertices.find(vertex => vertex.index === edge.source);
        const target = props.vertices.find(vertex => vertex.index === edge.target);
        return {
            source: {index: source.index, data: source.data},
            target: {index: target.index, data: target.data},
            weight: edge.weight,
            data: edge.data
        }
    });

    fetch('http://localhost:8080/save?user_id=' + userID, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Connection': 'keep-alive'
        },
        body: JSON.stringify({
            userID: userID,
            graph: {
                vertices: props.vertices,
                edges: edges,
                info: {
                    graphName: graphName,
                    isDirected: props.isDirected,
                    isWeighted: props.isWeighted,
                    hasSelfLoops: hasSelfLoops,
                    hasMultipleEdges: hasMultipleEdges
                }
            },
            comment: comment
        }),
    })
    .then(response => console.log(response))
    .catch(error => console.log(error));
  }


    const setFlowResearch = (vertex, isSource) => {
      if (props.vertices !== null) {
        const v = props.vertices.find(v => v.data === vertex);
        if (isSource) {
          setSource(v.index.toString());
        } else {
          setSink(v.index.toString());
        }
      }
    }


  return (
    <div >
      <div>
        <p className='title'>Graph Researcher</p>
        </div>
      <div className='menu'>
        <div>
            {
              userID !== -1 ? <div>  <UserGraphs  addVertex={props.addVertex}
                                                  addEdge={props.addEdge}
                                                  updateVertexCount={props.updateVertexCount}
                                                  setEdgeCounter={props.setEdgeCounter}
                                                  setIsDirected={props.setIsDirected}
                                                  setIsWeighted={props.setIsWeighted}
                                                  setHasSelfLoops={props.setHasSelfLoops}
                                                  sethasMultipleEdges={props.sethasMultipleEdges}
                                                  setIsUserGraphMode={props.setIsUserGraphMode}
                                                  setGraphName={setGraphName}
                                                  setGraphResearchInfo={setGraphResearchInfo}
                                                  setComment={setComment}/>
                                <div style={{paddingTop: '1.5rem'}}>
                                  <Button className='button' onClick={handleCreateVertexButton} type='text' block> Create vertex</Button>
                                </div>
                              </div>
                            : <div>
                                <Button className='button' onClick={handleCreateVertexButton} type='text' block> Create vertex</Button>
                              </div>
            }
        </div>
        <div style={{paddingTop: '1.5rem'}}>
          <Button className='button' onClick={handleCreateEdgeButton} type='text' block> Create edge</Button>
        </div>
        <div style={{paddingTop: '1.5rem'}}>
          <Button className='button' onClick={handleRemoveVertexButton} type='text' block> Remove vertex</Button>
        </div>
        <div style={{paddingTop: '1.5rem'}}>
        <Button className='button' onClick={handleRemoveEdgeButton} type='text' block> Remove edge</Button>
        </div>
        <div style={{paddingTop: '1.5rem'}}>
            <ResearchInfo vertices={props.vertices}
                           edges={props.edges}
                           isWeighted={props.isWeighted}
                           isDirected={props.isDirected}
                           hasMultipleEdges={props.hasMultipleEdges}
                           hasSelfLoops={props.hasSelfLoops}
                           graphName={graphName}
                           setGraphName={setGraphName}
                           source={source}
                           sink={sink}
                           setSource={setSource}
                           setSink={setSink}
                           comment={comment}
                           graphResearchInfo={graphResearchInfo}
                           setGraphResearchInfo={setGraphResearchInfo}/>
        </div>
        {
          props.isDirected && props.isWeighted &&
          <div>
            <div style={{paddingTop: '1.5rem', paddingLeft: '1.7rem'}}>
              <Input type='text' value={source} onChange={e => setFlowResearch(e.target.value, true)} style={{width: '94px'}} placeholder='source'/>
            </div>
            <div style={{paddingTop: '1.5rem', paddingLeft: '1.7rem'}}>
              <Input type='text' value={sink} onChange={e => setFlowResearch(e.target.value, false)} style={{width: '94px'}} placeholder='target'/>
            </div>
          </div>
        }
        <div style={{paddingTop: '1.5rem', paddingLeft: '1.7rem'}}>
          <div>
            <label> 
              <Checkbox onChange={handleIsDirectedCheckbox} > Directed </Checkbox>
            </label>
          </div>
        </div>
        <div style={{paddingTop: '1.5rem', paddingLeft: '1.7rem'}}>
          <div>
            <label>
              <Checkbox onChange={handleIsWeightedCheckbox}>
                Weighted
              </Checkbox>
            </label>
          </div>
        </div>
        <div style={{paddingTop: '1.5rem'}}>
            <GraphArchive addVertex={props.addVertex}
                          addEdge={props.addEdge}
                          updateVertexCount={props.updateVertexCount}
                          setEdgeCounter={props.setEdgeCounter}
                          setIsDirected={props.setIsDirected}
                          setIsWeighted={props.setIsWeighted}
                          setHasSelfLoops={props.setHasSelfLoops}
                          sethasMultipleEdges={props.sethasMultipleEdges}
                          setIsGraphArchiveMode={props.setIsGraphArchiveMode}
                          setGraphName={setGraphName}/>
        </div>
        {
            userID === -1 &&  <div style={{paddingTop: '1.5rem'}}>
                                <Button className='button' onClick={goToSignInPage} type='text' block> Sign In</Button>
                              </div>
        }
        {
          userID !== -1 && <div style={{paddingTop: '1.5rem'}}>
                            <Button className='button' onClick={() => {isGraphSaveMode ? setIsGraphSaveMode(false)
                                                   : setIsGraphSaveMode(true)}} type='text' block> Save Graph</Button>
                            <div style={{paddingTop: '1.5rem'}}>
                            <Button className='button' type='text' block onClick={() => {isAddCommentMode ? setAddCommentMode(false)
                                                   : setAddCommentMode(true)}}> Add comment</Button> 
                            </div>
                           </div>
        }
      </div>
      {
        isAddCommentMode && <div className='background'>
        <div className='commentBody'>
          <Input.TextArea value={comment} placeholder='add comment' rows={5} onChange={e =>setComment(e.target.value)}/>
        </div>
      </div>
      }     
      {
        isGraphSaveMode && <div className='background'>
        <div className='nameBody'>
          <Input style={{width: '200px'}} onChange={e => {setGraphName(e.target.value)}} placeholder='enter graph name'/>
          <Button onClick={saveGraph}>Submit</Button>
        </div>
        </div>
      } 
      <div className='canvas'>
        <canvas
                id='canvas'
                width={window.innerWidth-10}
                height={window.innerHeight-114}>
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
  setEdgeCounter: PropTypes.func,
  setHasSelfLoops: PropTypes.func,
  sethasMultipleEdges: PropTypes.func,
  vertices: PropTypes.array,
  edges: PropTypes.array,
  setIsGraphArchiveMode: PropTypes.func,
  setIsUserGraphMode: PropTypes.func
};

export default Researcher;

