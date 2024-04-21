import React, { useLayoutEffect } from "react";
import VertexButton from "./components/VertexButton";
import Vertex from "./components/Vertex";
import GraphMetadata from "./components/GraphResearchInfo"

function App() {
  const [vertices, setVertex] = React.useState([]);
  const [count, updateCount] = React.useState(1);
  const [edges,setEdges]=React.useState([]);
  const [edgeCounter,setEdgeCounter]=React.useState(1);
  const [edgeRemove,setEdgeRemove]=React.useState(false);
  const [edgeCreate,setEdgeCreate]=React.useState(false);

  function createVertex() {
    setEdgeRemove(false);
    setEdgeCreate(false);
    const newCount = count + 1
    updateCount(newCount)
    setVertex([...vertices,
      {id: count, 
       vertex: <Vertex key={count} id={count} data={count}/>,
       x: 0, y: 0}]
    )
  }

  function updateButtonCoordinates (id, x, y){    
    setVertex(vertices.map(vertex => {
      return vertex.id === id ? { ...vertex, x, y } : vertex}
      ))
  }

  function createEdge(ids,idt){
    setEdgeRemove(false);
    const newCounter=edgeCounter+1
    setEdgeCounter(newCounter)

    setEdges([...edges,{source: ids,
    target: idt,
    id: edgeCounter
    }])
    console.log(edges)
  }

  function isEdgeCreate(){
    setEdgeRemove(false);
    setEdgeCreate(true);
  }

  function removeEdge(){
    setEdgeCreate(false);
    setEdgeRemove(true);
  }


  function deleteEdge(ids,idt){
    const newCounter=edgeCounter-1
    setEdgeCounter(newCounter)
    console.log(ids, idt)
    setEdges(edges.filter(edge => !(edge.source === ids & edge.target === idt) && !(edge.source === idt & edge.target === ids)))
    console.log(edges)
  }

  const draw = (ids,idt)=>{
    const canvas = document.getElementById("canvas");
    const ctx = canvas.getContext("2d");
    ctx.lineWidth=4;
    const v1=vertices.find(element => element.id===ids);
    const v2=vertices.find(element => element.id===idt);
    ctx.beginPath();
    ctx.moveTo(v1.x,v1.y);
    ctx.lineTo(v2.x,v2.y);
    ctx.stroke();
};

  useLayoutEffect(() =>{
    const canvas=document.getElementById('canvas');
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0,0,canvas.width, canvas.height);
    edges.forEach(element=>{
      draw(element.source,element.target)
    })
  });

  return (
    <div >
      <div >
        <p className='title'>Graph Researcher</p>
      </div>
      <div className="menu">
        <div ><button className='button' onClick={() => createVertex()}> Create vertex</button>
            <VertexButton vertices={vertices} 
                          updateButtonCoordinates={updateButtonCoordinates} 
                          createEdge={createEdge} 
                          edgeRemove={edgeRemove} 
                          edgeCreate={edgeCreate} 
                          deleteEdge={deleteEdge}/>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={() => isEdgeCreate()}> Create edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <button className='button' onClick={() => removeEdge()}> Remove edge</button>
        </div>
        <div style={{paddingTop: "3rem"}}>
          <GraphMetadata setEdgeCreate={setEdgeCreate}/>
        </div>    
      </div>        
      <div>
        <canvas   className="canvas"
                id="canvas"
                width={window.innerWidth-2}
                height={window.innerHeight-112}>
                Canvas
        </canvas>
      </div>
    </div>
  );
}

export default App;

