import React, {useRef, useEffect} from 'react';
import './Researcher.css'
import Researcher from "./Researcher"
import Vertex from "./Vertex";
import Edge from './Edge';

function Graph() {
    const [vertices, setVertex] = React.useState([]);
    const [count, updateCount] = React.useState(1);
    const [edges,setEdges]=React.useState([]);
    const [edgeCounter,setEdgeCounter]=React.useState(1);
    const [edgeRemove,setEdgeRemove]=React.useState(false);
    const [edgeCreate,setEdgeCreate]=React.useState(false);
    const [vertexRemove,setVertexRemove]=React.useState(false);
    const [isDirected,setIsDirected]=React.useState(false);
    const [isWeighted,setIsWeighted]=React.useState(false);
    const [coordinates, updateCoordinates] = React.useState([]);
    const verticesRef = useRef([]);
    const [drawEdge,setDrawEdge]=React.useState(false);
    const [source,setSource]=React.useState(0);
    const [id, setId] = React.useState(0);

  
    function createVertex(xx, yy) {           //OK
      setVertexRemove(false);
      setEdgeRemove(false);
      setEdgeCreate(false);
      const newCount = count + 1
      updateCount(newCount)
      setVertex([...vertices,
        {index: count,
         x: xx, y: yy,
         data: count.toString()}]
      )
    }
  
    function updateButtonCoordinates (id, x, y){                   //TODO
      setVertex(vertices.map(vertex => {
        return vertex.index === id ? { ...vertex, x, y } : vertex}
        )); 
    }

    function createEdgee(id){                    //OK
        if (edgeCreate === true){
            if(drawEdge===true){
                    setVertexRemove(false);
                    setEdgeRemove(false);
                    setEdgeCreate(false);
                    const newCounter=edgeCounter+1
                    setEdgeCounter(newCounter)
                    setEdges([...edges,{source: source,
                    target: id,
                    id: edgeCounter,
                    data: '',
                    weight: 1.0
                    }])
                    setDrawEdge(false);
            }else{
                setSource(id);
                setDrawEdge(true);
            }
        } 
        console.log(edges);
    }
  
    function deleteVertexx(id){             //OK
        if (vertexRemove === true){
            console.log('skuf');
            setVertex(vertices.filter(vertex => (vertex.index !== id)))
            setEdges(edges.filter(edge => ((edge.source !== id) & (edge.target !== id))))
        }
        setVertexRemove(false);
    }


    function deleteEdgee(id){              //OK
        if (edgeRemove === true){
            if(drawEdge===true){
                const newCounter = edgeCounter-1
                setEdgeCounter(newCounter)
                setEdges(edges.filter(edge => !(edge.source === source & edge.target === id) && !(edge.source === id & edge.target === source)))
                setDrawEdge(false);
                setEdgeRemove(false);
            }else{
                setSource(id);
                setDrawEdge(true);
            }
        }
    }

    const handleMouseMove = (id) => () => {
        setId(id);    
        createEdgee(id);
        deleteEdgee(id);
        deleteVertexx(id);
    };


    useEffect(() => {                                        // TODO
      const drawVertex = (event) => {
        updateCoordinates([event.clientX, event.clientY]);
      }
  
      const handleKeyPress = (event) => {
        if (event.key === 'v'){
          createVertex(coordinates[0], coordinates[1]);
        }
      };
  
      document.addEventListener('mousemove', drawVertex);
      window.addEventListener('keypress', handleKeyPress);
  
      return (() => {
        document.removeEventListener('mousemove', drawVertex);
        window.removeEventListener('keypress', handleKeyPress);
      })
    });



    useEffect(() => {                                                          //TODO
        const handleKeyPress = (event) => {
          if (event.key === 'd'){
            const v = document.getElementsByClassName('vertex');
            const a = Array.prototype.find.call(v, ver => (
                coordinates[1] <= ver.getBoundingClientRect().bottom &&
                coordinates[1] >= ver.getBoundingClientRect().top &&
                coordinates[0] <= ver.getBoundingClientRect().right &&
                coordinates[0] >= ver.getBoundingClientRect().left
            ));
            if (a) {
                console.log(a.children[0].value);
                setVertexRemove(true);
                deleteVertexxx(parseInt(a.children[0].value));
            }
          }
        };
    
        function deleteVertexxx(id){
            deleteVertexx(id);
        }

        window.addEventListener('keypress', handleKeyPress);
    
        return (() => {
          window.removeEventListener('keypress', handleKeyPress);
        })
      });

    
    useEffect(() => {
        const mouseMove = (event) => {
            if (id !== 0) {
                const rect = event.target.getBoundingClientRect();
                const v = document.getElementsByClassName('vertex');
                const a = Array.prototype.find.call(v, ver => (
                    rect.y <= ver.getBoundingClientRect().bottom &&
                    rect.y >= ver.getBoundingClientRect().top &&
                    rect.x <= ver.getBoundingClientRect().right &&
                    rect.x >= ver.getBoundingClientRect().left
                ));
                if (a) {
                    updateButtonCoordinates(id,
                        a.getBoundingClientRect().left + (a.getBoundingClientRect().right - a.getBoundingClientRect().left) / 2,
                        a.getBoundingClientRect().top + (a.getBoundingClientRect().top - a.getBoundingClientRect().bottom) / 2 - 55
                    );
                }
            }
        };

        const mouseUp = () => {
            setId(0);
        };

        window.addEventListener('mousemove', mouseMove);
        window.addEventListener('mouseup', mouseUp);

        return () => {
            window.removeEventListener('mousemove', mouseMove);
            window.removeEventListener('mouseup', mouseUp);
        };
    })

        return (
            <div >
              
              <div>
                    {vertices ? vertices.map(vertex => {
                        return (
                            <div 
                                key={vertex.index} 
                                ref={ref => verticesRef.current[vertex.index] = ref}
                                onMouseDown={handleMouseMove(vertex.index)}
                            >
                                <Vertex index={vertex.index} x={vertex.x} y={vertex.y} vertices={vertices} setVertex={setVertex} data={vertex.data}/>
                            </div>
                        );
                    }) : null}  
                </div>
                <div>
                <Edge isWeighted={isWeighted}
                      vertices={vertices}
                      edges={edges}
                      isDirected={isDirected}/>
                </div>
                <div>
                    <Researcher createVertex={createVertex}
                                setVertexRemove={setVertexRemove}
                                setEdgeRemove={setEdgeRemove}
                                setEdgeCreate={setEdgeCreate}
                                setIsDirected={setIsDirected}
                                setIsWeighted={setIsWeighted}
                                isWeighted={isWeighted}
                                isDirected={isDirected}
                                vertices={vertices}
                                setVertex={setVertex}
                                edges={edges}/>

                </div>
            </div>
          );
        }

export default Graph;