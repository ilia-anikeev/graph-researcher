import React, { useState } from "react";
import PropTypes from 'prop-types';
import "./GraphMetadata.css";
import "../index.css"

function GraphMetadata(props){
    const [isOpen, setState] = useState(false)

    const research = () => {
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
        })
        fetch('http://localhost:8080/research', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Connection': 'keep-alive'
            },
            body: JSON.stringify({
                userID: 0,
                graph: {
                    vertices: props.vertices,
                    edges: edges,
                    info: {
                        isDirected: props.isDirected,
                        isWeighted: props.isWeighted,
                        hasSelfLoops: hasSelfLoops,
                        hasMultipleEdges: hasMultipleEdges
                    }
                }
            }),
        })
        .then(response => console.log(response.text()))
        .catch(error => console.log(error));
    }

    return (
        <div>
            <button className="button" onClick={() => {
                    research();
                }
            }>      
                    Research      
            </button>
            {isOpen && <div className="GraphMetadata">
                <div className="GraphMetadata-body" >
                    <h1 style={{textAlign: "center"}}>Info</h1>
                    <p>Bridges                                                                     no </p>  
                    <p>Articulation Points                                                 3</p>
                    <p>Connected Components                              (1, 2, 3, 4, 5)</p>
                    <button style={{alignSelf: 'right'}} onClick={() => setState(false)}>Close</button>
                </div>
            </div>
            }
        </div>
    )
}

export default GraphMetadata;

GraphMetadata.propTypes = {
    vertices: PropTypes.array,
    edges: PropTypes.array,
    isWeighted: PropTypes.bool,
    isDirected: PropTypes.bool,
    hasSelfLoops: PropTypes.number,
    hasMultipleEdges: PropTypes.number,
}