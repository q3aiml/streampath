/**
 * Taking a string streampath expression and finally returning it's value happens in 3 main phases:
 * <ol>
 * <li>
 *     Parsing/compilation.
 *     <p/>
 *     This phase converts the string expression into an
 *     <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">AST</a>, which is a tree of objects representing
 *     the expression.
 *     <p/>
 *     This is mainly handled by the <a href="https://github.com/sirthias/parboiled">parboiled</a>
 *     parser in {@link net.q3aiml.streampath.lang}, see {@link net.q3aiml.streampath.lang.Parser}, though the parser
 *     is created and used in {@link net.q3aiml.streampath.StreamPath}. The AST nodes are
 *     {@link net.q3aiml.streampath.ast.Expression} or {@link net.q3aiml.streampath.ast.StreamPathNode} (in the case
 *     of parts of selectors).
 * </li>
 * <li>
 *     Aggregation/document input.
 *     <p/>
 *     This phase reads each document in, calculating aggregates defined in the expression.
 *     <p/>
 *     Documents are read as {@link net.q3aiml.streampath.evaluator.Frame}s.
 *     {@link net.q3aiml.streampath.ast.aggregate.AggregatorNode}s,
 *     using {@link net.q3aiml.streampath.ast.aggregate.Aggregator}s, define how to roll up zero or more frames
 *     into a single value, while {@link net.q3aiml.streampath.ast.selector.Selector}s referenced by the aggregator
 *     AST node select which frames should be added to an aggregation. Most of this is orchestrated by the
 *     {@link net.q3aiml.streampath.evaluator.Aggregator3000}.
 * </li>
 * <li>
 *     Evaluation.
 *     <p/>
 *     This final major phase produces the ultimate value for each expression.
 *     <p/>
 *     Evaluation is performed by the {@link net.q3aiml.streampath.evaluator.Evaluator}, combining aggregate
 *     values built up by the {@link net.q3aiml.streampath.evaluator.Aggregator3000} with logic specified
 *     in AST {@link net.q3aiml.streampath.ast.Expression}s.
 * </li>
 * </ol>
 */
package net.q3aiml.streampath;