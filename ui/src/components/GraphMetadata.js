import React, { useState, useContext } from 'react';
import PropTypes from 'prop-types';
import './GraphMetadata.css';
import { UserContext } from './UserContex';
import '../index.css';

function GraphMetadata(props){
    const [isOpen, setState] = useState(false);
    const [graphMetaData, setGraphMetaData] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [isGraphSaveMode, setIsGraphSaveMode] = useState(false);
    const { userID } = useContext(UserContext);

    const getCorrectData = () => {
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
        return {hasMultipleEdges, hasSelfLoops, edges}
    }

    const getGraphMetadata = () => {
        setState(true);
        
        const {hasMultipleEdges, hasSelfLoops, edges} = getCorrectData();

        fetch('http://localhost:8080/research', {
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
                        graphName: 'graphName',
                        isDirected: props.isDirected,
                        isWeighted: props.isWeighted,
                        hasSelfLoops: hasSelfLoops,
                        hasMultipleEdges: hasMultipleEdges
                    }

                }
            }),
        })
        .then(response => response.json())
        .then(metaData => {setGraphMetaData(metaData)})
        .catch(error => {
            setErrorMessage('Something went wrong, try again'); 
            setGraphMetaData(null);
        });
    }

    const getString = (data) => {
        var stringData = '';
        if (Array.isArray(data) && data.length !== 0) {
            for (let i = 0; i < data.length; ++i) {
                if (getString(data[i]) === '') {
                    continue;
                }
                stringData += stringData === '' ? getString(data[i]) : ', ' + getString(data[i]); 
            }
        }
        if (data != null && 'source' in data && 'target' in data) {
            stringData += stringData === '' 
                          ? '{' + data.source.data.toString() + '-' + data.target.data.toString() + '}'
                          : ', {' + data.source.data.toString() + '-' + data.target.data.toString() + '}';
        } else 
        if (data != null && 'data' in data) {
            stringData += stringData === '' ? data.data.toString() : ', ' + data.data.toString();
        }
        return Array.isArray(data) && stringData !== '' && stringData[0] !== '[' ? '[' + stringData + ']' : stringData;
    }

    const getStringData = (data) => {
        if (Array.isArray(data)) {
            return getString(data) === '' ? 'no' : getString(data);
        }
        if (typeof data === 'number') {
            return data.toString();
        }
        return data ? 'yes' : 'no';
    }

   const getStringKey = (key) => {
        switch (key){
            case 'isConnected':
                return 'Connected';
            case 'isBiconnected':
                return 'Biconnected';
            case 'articulationPoints':
                return 'Articulation Points';
            case 'bridges':
                return 'Bridges'
            case 'connectedComponents':
                return 'Connected Components';
            case 'blocks':
                return 'Blocks';        
            case 'isPlanar':
                return 'Planar';
            case 'embedding':
                return 'Embedding';
            case 'kuratowskiSubgraph':
                return 'Kuratowski Subgraph';
            case 'isChordal':
                return 'Chordal';
            case 'perfectEliminationOrder':
                return 'Perfect Elimination Order';
            case 'chromaticNumber':
                return 'Chromatic Number';
            case 'coloring':
                return 'Coloring';
            case 'maxClique':
                return 'Max Clique';
            case 'independentSet':
                return 'Independent Set';
            case 'minimal_vertex_separator':
                return 'Minimal Vertex Separator';
            case 'isBipartite':
                return 'Bipartite';    
            case 'partitions':
                return 'Partitions';
            case 'min_spanning_tree':
                return 'Min Spanning Tree';
            default :
                return '';    
        }
    }    

    const saveGraph = () => {
        const {hasMultipleEdges, hasSelfLoops, edges} = getCorrectData();
        fetch('http://localhost:8080/save', {                              //TODO
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
                        graphName: props.graphName,
                        isDirected: props.isDirected,
                        isWeighted: props.isWeighted,
                        hasSelfLoops: hasSelfLoops,
                        hasMultipleEdges: hasMultipleEdges
                    }

                }
            }),
        })
        .catch(error => console.log(error));
        setIsGraphSaveMode(false);
    }


    return (
        <div>
            <button className='button' onClick={getGraphMetadata}>      
                    Research      
            </button>
            {isOpen && <div className='GraphMetadata'>
                <div className='GraphMetadata-body'>
                    <h1 style={{textAlign: 'center'}}>Info</h1>
                    {graphMetaData ? Object.keys(graphMetaData).map(key => {
                        return (
                            <div>
                                <p> {getStringKey(key.toString())} : {getStringData(graphMetaData[key])} </p>
                            </div>
                        )
                    }) : 
                        <p>
                            {errorMessage}
                        </p>
                    }
                    <div>
                        {
                         userID !== -1 && <button style={{alignSelf: 'left'}} 
                                onClick={() => setIsGraphSaveMode(true)}> Save </button>
                        }
                        {
                            isGraphSaveMode && 
                            <input type='text'placeholder='enter graph name' onChange={(e) => props.setGraphName(e.target.value)}/>
                        }
                        {
                            isGraphSaveMode && <button onClick={saveGraph}>Submit</button>
                        }
                    </div>
                    <div style={{paddingTop: '1rem'}}>
                        <button onClick={() => setState(false)}>Close</button>
                    </div>
                </div>
            </div>
            }
        </div>
    )
}

GraphMetadata.propTypes = {
    vertices: PropTypes.array,
    edges: PropTypes.array,
    isWeighted: PropTypes.bool,
    isDirected: PropTypes.bool,
    hasSelfLoops: PropTypes.number,
    hasMultipleEdges: PropTypes.number,
    graphName: PropTypes.string,
    setGraphName: PropTypes.func
}

export default GraphMetadata;
