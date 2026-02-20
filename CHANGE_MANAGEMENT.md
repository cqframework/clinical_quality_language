# Overview
This is a meritocratic, consensus-based community project. Anyone with interest in the project can join the community, contribute to the project design and participate in the decision making process. Participation in these processes is completely voluntary. This document describes how that participation takes place and how to set about earning merit within the project community.

Although these processes are adapted from the [OSSWatch Meritocratic Model](http://oss-watch.ac.uk/resources/meritocraticgovernancemodel), this documentation is a formalization of existing processes involving relevant stakeholders.

## Roles
### Stakeholders
Stakeholders are community members who have a need for the tooling provided by this project. They are the most important members of the community and without them the project would have no purpose. Anyone can be a stakeholder; there are no special requirements.

Stakeholder participation is on a voluntary basis, but the project asks its stakeholders to participate in the project and community as much as possible. Stakeholder contributions enable the project team to ensure that they are satisfying the requirements of those stakeholders. Common stakeholder contributions include (but are not limited to):
* Evangelism of the project (e.g. a link on a website and word-of-mouth awareness)
* Informing developers of strengths and weaknesses from a new stakeholder perspective
* Providing moral support (a ‘thank you’ goes a long way)
* Stakeholders who continue to engage with the project and its community will often become more and more involved. Such stakeholders may find themselves becoming contributors, as described in the next section.

### Contributors
Contributors are community members who contribute in concrete ways to the project. Anyone can become a contributor, and contributions can take many forms. There is no expectation of commitment to the project, no specific skill requirements and no selection process.

In addition to their actions as stakeholders, contributors may also find themselves doing one or more of the following:
* Supporting new users (existing users are often the best people to support new users)
* Reporting bugs
* Identifying requirements
* Providing graphics and web design
* Programming
* Assisting with project infrastructure
* Writing documentation
* Fixing bugs
* Adding features

Contributors engage with the project through the issue tracker and mailing list, or by writing or editing documentation. They submit changes to the project itself via pull requests, which will be considered for inclusion in the project by existing committers (see next section). The mailing list is the most appropriate place to ask for help when making that first contribution.

As contributors gain experience and familiarity with the project, their profile within, and commitment to, the community will increase. At some stage, they may find themselves being nominated for committership.

### Committers

Committers are community members who have shown that they are committed to the continued development of the project through ongoing engagement with the community. New committers can be nominated by any existing committer. Once they have been nominated, there will be a vote by the project management committee (PMC; see below). Committer voting is one of the few activities that takes place on the project’s private management list. This is to allow PMC members to freely express their opinions about a nominee without causing embarrassment. Once the vote has been held, the aggregated voting results are published on the public mailing list. The nominee is entitled to request an explanation of any ‘no’ votes against them, regardless of the outcome of the vote. This explanation will be provided by the PMC Chair (see below) and will be anonymous and constructive in nature.

It is important to recognize that commitership is a privilege, not a right. That privilege must be earned and once earned it can be removed by the PMC (see next section) in extreme circumstances. However, under normal circumstances committership exists for as long as the committer wishes to continue engaging with the project.

Committers are managed as members of the [cql-tooling](https://github.com/orgs/cqframework/teams/cql-tooling) team in this repository.

A committer who shows an above-average level of contribution to the project, particularly with respect to its strategic direction and long-term health, may be nominated to become a member of the PMC. This role is described below.

### Project management committee
The project management committee consists of those individuals identified as ‘project owners’ on the development site. The PMC is responsible for ensuring the smooth running of the project, including strategic planning, release cycles, change management, and changes to project governance.

Membership of the PMC is by invitation from the existing PMC members. A nomination will result in discussion and then a vote by the existing PMC members. PMC membership votes are subject to consensus approval of the current PMC members.

#### PMC Chair
The PMC Chair is a single individual, voted for by the PMC members. Once someone has been appointed Chair, they remain in that role until they choose to retire, or the PMC casts a two-thirds majority vote to remove them.

The PMC Chair has no additional authority over other members of the PMC: the role is one of coordinator and facilitator. The Chair is also expected to ensure that all governance processes are adhered to, and has the casting vote when the project fails to reach consensus.

**PMC Membership**
* [Bryn Rhodes](https://github.com/brynrhodes) (PMC Chair)
* [Chris Moesel](https://github.com/cmoesel)
* [Marc Hadley](https://github.com/hadleynet)

### Support
All participants in the community are encouraged to provide support for new users within the project management infrastructure. This support is provided as a way of growing the community. Those seeking support should recognise that all support activity within the project is voluntary and is therefore provided as and when time allows. A user requiring guaranteed response times or results should therefore seek to purchase a support contract from a community member. However, for those willing to engage with the project on its own terms, and willing to help support other users, the community support channels are ideal.

## Decision making process
Decisions on the project such as whether to accept a change, add a feature, or what to include in a particular release, are made through discussion with all members of the community. Anyone is welcome to comment on proposed changes, but ultimately, the committers make the determination on accepting or rejecting a proposed change. Where committer consensus cannot be reached, the decision is escalated to the PMC.
In order to ensure that the project is not bogged down by endless discussion and continual voting, the project operates a policy of lazy consensus. This allows the majority of decisions to be made without resorting to a formal vote.

### Lazy consensus
Decision making typically involves the following steps:
* Proposal
* Discussion
* Vote (if consensus is not reached through discussion)
* Decision

Any community member can make a proposal for consideration by the community. In order to initiate a discussion about a new idea, they should submit an issue to the project issue tracker, or a pull request implementing the idea. This will prompt a review and, if necessary, a discussion of the idea. The goal of this review and discussion is to gain approval for the contribution.

In general, as long as nobody explicitly opposes a proposal or change, and the functionality is aligned with the CQL specification, it is recognized as having the support of the community. This is called lazy consensus - that is, those who have not stated their opinion explicitly have implicitly agreed to the implementation of the proposal.

For lazy consensus to be effective, it is necessary to allow sufficient time for stakeholders to review before assuming that there are no objections to the proposal. The more significant the issue, the more time is required for review, with the intent that everyone is given enough time to read, digest, and respond to the proposal. The time given for review will be chosen so as to be as inclusive as possible of all participants, regardless of their location and time commitments.

### Voting
Not all decisions can be made using the process of lazy consensus. Issues such as those affecting the strategic direction, project releases, or legal standing of the project must gain explicit approval in the form of a vote. Every member of the community is encouraged to express their opinions in all discussion and all votes. However, only project committers and/or PMC members (as defined above) have binding votes for the purposes of decision making.

# Contributions
The project uses a stable-trunk methodology, meaning that the main branch must be kept in a releasable state at all times. This is ensured through regression tests and continuous integration is used to check pull requests to the main branch.

Only trivial commits (such as documentation changes, typos, and packaging changes) can be made directly to the main branch. All other changes to the main branch must be made using a pull request, and must be reviewed prior to being applied.

Small changes must be reviewed by at least one committer that is not the author of the change. Large, significant, or breaking changes must have committer consensus before being applied.

In general, when a new version of the CQL specification is created, a new branch of the repository is created to implement support for that version. Changes to this branch can be committed directly. Once the new version of the specification is published, the version branch is merged to the main branch.

Note that changes must always align with the CQL specification. Any changes to the CQL specification itself are governed by HL7 and must be done through the HL7 process, either by submitting an STU comment, or balloting a new version of the specification.

# Communication Channels
Communication should be done in an open and public manner. The project uses many different channels for open communication, including:

* [Chat](https://chat.fhir.org/#narrow/stream/cql)
* [Github Issues](https://github.com/cqframework/clinical_quality_language/issues)
* _deprecated_ Mailing List: [cql-tooling@googlegroups.com](https://groups.google.com/forum/?hl=en#!forum/cql-tooling)

Sometimes, communication occurs outside of these public channels, and that is okay; however, committers must summarize any private discussions that impact the tooling project in a public channel.

# Code of Conduct
In support of a healthy and inclusive community, we use and enforce a [code of conduct](CODE_OF_CONDUCT.md) for all members of our community, including committers and PMC members. Our code of conduct is adapted from the [Contributor Covenant](https://www.contributor-covenant.org/).

If you encounter any violation of these terms, please contact the PMC Chair or a PMC committee member. All reports will be kept in strict confidence and dealt with promptly.

# Issues
This project uses the Github Issues tracker to track all issues and feature requests. Anyone can submit an issue at any time. Committers are generally responsible for reviewing, responding to, and resolving issues in a timely manner.

# Releases
All packages within the project shall use semantic versioning. Any stakeholder can propose a release, but the PMC must review and approve the contents and timing of any release. Specifically, releases must be coordinated with impacted stakeholders and timed with availability of published versions of the specifications involved.

The PMC Chair is responsible for announcing releases to the community via the zulip chat. When appropriate, the PMC Chair should also announce plans for upcoming releases to solicit feedback from the community on release content and timing.

